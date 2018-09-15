package cn.lonsun.key;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author gu.fei
 * @version 2016-07-21 11:24
 */
public class SequenceGenerator implements IdentifierGenerator, Configurable {

	private String tableName;

	private String idName;

	@Override
	public Serializable generate(SessionImplementor session, Object object)
			throws HibernateException {
		return getNext(session);
	}

	@Override
	public void configure(Type type, Properties params, Dialect d)
			throws MappingException {
		tableName = params.getProperty("table");
		if (tableName==null)
			tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);

		idName = params.getProperty(PersistentIdentifierGenerator.PK);
	}

	private Long getNext(SessionImplementor session) throws HibernateException {
		return PrimaryKeyUtil.getPrimaryKey(tableName, idName);
	}
}
