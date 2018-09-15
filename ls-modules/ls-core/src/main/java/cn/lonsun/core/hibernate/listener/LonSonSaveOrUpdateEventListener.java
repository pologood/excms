package cn.lonsun.core.hibernate.listener;

import cn.lonsun.core.hibernate.SnowFlakeIdGenerater;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.PersistentObjectException;
import org.hibernate.TransientObjectException;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.id.IdentifierGenerationException;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;

/**
 * 此处覆盖hibernate的方法，修改保存时的主键生成策略
 * @author zhongjun
 */
public class LonSonSaveOrUpdateEventListener extends DefaultSaveOrUpdateEventListener implements SaveOrUpdateEventListener {

    private static final CoreMessageLogger LOG = CoreLogging.messageLogger( LonSonSaveOrUpdateEventListener.class );

    private boolean useHibernateIdGenerater = true;

    @Override
    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
        final SessionImplementor source = event.getSession();
        final Object object = event.getObject();
        final Serializable requestedId = event.getRequestedId();

        if ( requestedId != null ) {
            //assign the requested id to the proxy, *before*
            //reassociating the proxy
            if ( object instanceof HibernateProxy) {
                ( (HibernateProxy) object ).getHibernateLazyInitializer().setIdentifier( requestedId );
            }
        }

        // For an uninitialized proxy, noop, don't even need to return an id, since it is never a save()
        if ( reassociateIfUninitializedProxy( object, source ) ) {
            LOG.trace( "Reassociated uninitialized proxy" );
        }
        else {
            //initialize properties of the event:
            final Object entity = source.getPersistenceContext().unproxyAndReassociate( object );
            event.setEntity( entity );
            event.setEntry( source.getPersistenceContext().getEntry( entity ) );
            //return the id in the event object
            event.setResultId( performSaveOrUpdate( event ) );
        }
    }

    protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
        EntityState entityState = getEntityState(
                event.getEntity(),
                event.getEntityName(),
                event.getEntry(),
                event.getSession()
        );

        switch ( entityState ) {
            case DETACHED:
                entityIsDetached( event );
                return null;
            case PERSISTENT:
                return entityIsPersistent( event );
            default: //TRANSIENT or DELETED
                return entityIsTransient( event );
        }
    }

    protected Serializable entityIsPersistent(SaveOrUpdateEvent event) throws HibernateException {
        final boolean traceEnabled = LOG.isTraceEnabled();
        if ( traceEnabled ) {
            LOG.trace( "Ignoring persistent instance" );
        }
        EntityEntry entityEntry = event.getEntry();
        if ( entityEntry == null ) {
            throw new AssertionFailure( "entity was transient or detached" );
        }
        else {

            if ( entityEntry.getStatus() == Status.DELETED ) {
                throw new AssertionFailure( "entity was deleted" );
            }

            final SessionFactoryImplementor factory = event.getSession().getFactory();

            Serializable requestedId = event.getRequestedId();

            Serializable savedId;
            if ( requestedId == null ) {
                savedId = entityEntry.getId();
            }
            else {

                final boolean isEqual = !entityEntry.getPersister().getIdentifierType()
                        .isEqual( requestedId, entityEntry.getId(), factory );

                if ( isEqual ) {
                    throw new PersistentObjectException(
                            "object passed to save() was already persistent: " +
                                    MessageHelper.infoString( entityEntry.getPersister(), requestedId, factory )
                    );
                }

                savedId = requestedId;

            }

            if ( traceEnabled ) {
                LOG.tracev(
                        "Object already associated with session: {0}",
                        MessageHelper.infoString( entityEntry.getPersister(), savedId, factory )
                );
            }

            return savedId;

        }
    }

    /**
     * The given save-update event named a transient entity.
     * <p/>
     * Here, we will perform the save processing.
     *
     * @param event The save event to be handled.
     *
     * @return The entity's identifier after saving.
     */
    protected Serializable entityIsTransient(SaveOrUpdateEvent event) {

        LOG.trace( "Saving transient instance" );

        final EventSource source = event.getSession();

        EntityEntry entityEntry = event.getEntry();
        if ( entityEntry != null ) {
            if ( entityEntry.getStatus() == Status.DELETED ) {
                source.forceFlush( entityEntry );
            }
            else {
                throw new AssertionFailure( "entity was persistent" );
            }
        }

        Serializable id = saveWithGeneratedOrRequestedId( event );

        source.getPersistenceContext().reassociateProxy( event.getObject(), id );

        return id;
    }

    /**
     * Save the transient instance, assigning the right identifier
     *
     * @param event The initiating event.
     *
     * @return The entity's identifier value after saving.
     */
    protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
        return saveWithGeneratedId(
                event.getEntity(),
                event.getEntityName(),
                null,
                event.getSession(),
                true
        );
    }

    /**
     * The given save-update event named a detached entity.
     * <p/>
     * Here, we will perform the update processing.
     *
     * @param event The update event to be handled.
     */
    protected void entityIsDetached(SaveOrUpdateEvent event) {

        LOG.trace( "Updating detached instance" );

        if ( event.getSession().getPersistenceContext().isEntryFor( event.getEntity() ) ) {
            //TODO: assertion only, could be optimized away
            throw new AssertionFailure( "entity was persistent" );
        }

        Object entity = event.getEntity();

        EntityPersister persister = event.getSession().getEntityPersister( event.getEntityName(), entity );

        event.setRequestedId(
                getUpdateId(
                        entity, persister, event.getRequestedId(), event.getSession()
                )
        );

        performUpdate( event, entity, persister );

    }

    /**
     * Determine the id to use for updating.
     *
     * @param entity The entity.
     * @param persister The entity persister
     * @param requestedId The requested identifier
     * @param session The session
     *
     * @return The id.
     *
     * @throws TransientObjectException If the entity is considered transient.
     */
    protected Serializable getUpdateId(
            Object entity,
            EntityPersister persister,
            Serializable requestedId,
            SessionImplementor session) {
        // use the id assigned to the instance
        Serializable id = persister.getIdentifier( entity, session );
        if ( id == null ) {
            // assume this is a newly instantiated transient object
            // which should be saved rather than updated
            throw new TransientObjectException(
                    "The given object has a null identifier: " +
                            persister.getEntityName()
            );
        }
        else {
            return id;
        }

    }

    /**
     * Prepares the save call using a newly generated id.
     *
     * @param entity The entity to be saved
     * @param entityName The entity-name for the entity to be saved
     * @param anything Generally cascade-specific information.
     * @param source The session which is the source of this save event.
     * @param requiresImmediateIdAccess does the event context require
     * access to the identifier immediately after execution of this method (if
     * not, post-insert style id generators may be postponed if we are outside
     * a transaction).
     *
     * @return The id used to save the entity; may be null depending on the
     *         type of id generator used and the requiresImmediateIdAccess value
     */
    @Override
    protected Serializable saveWithGeneratedId(
            Object entity,
            String entityName,
            Object anything,
            EventSource source,
            boolean requiresImmediateIdAccess) {
        EntityPersister persister = source.getEntityPersister( entityName, entity );
        //==================下面是自定义的代码=================================
        Serializable generatedId = null;
        if(useHibernateIdGenerater){
            generatedId = persister.getIdentifierGenerator().generate( source, entity );
        }else{
            generatedId = SnowFlakeIdGenerater.instance.nextId();
        }

        //==================自定义的代码结束=================================
        if ( generatedId == null ) {
            throw new IdentifierGenerationException( "null id generated for:" + entity.getClass() );
        }
        else if ( generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR ) {
            return source.getIdentifier( entity );
        }
        else if ( generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
            return performSave( entity, null, persister, true, anything, source, requiresImmediateIdAccess );
        }
        else {
            // TODO: define toString()s for generators
            if ( LOG.isDebugEnabled() ) {
                LOG.debugf(
                        "Generated identifier: %s, using strategy: %s",
                        persister.getIdentifierType().toLoggableString( generatedId, source.getFactory() ),
                        persister.getIdentifierGenerator().getClass().getName()
                );
            }

            return performSave( entity, generatedId, persister, false, anything, source, true );
        }
    }

    public boolean isUseHibernateIdGenerater() {
        return useHibernateIdGenerater;
    }

    public void setUseHibernateIdGenerater(boolean useHibernateIdGenerater) {
        this.useHibernateIdGenerater = useHibernateIdGenerater;
    }
}
