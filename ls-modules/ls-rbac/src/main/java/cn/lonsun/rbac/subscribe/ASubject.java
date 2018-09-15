package cn.lonsun.rbac.subscribe;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.task.TaskExecutor;

import cn.lonsun.core.util.SpringContextHolder;

/**
 * 新增、修改或删除人员信息时会涉及到其他许多与人员本身无关的业务，因此通过观察者模式完成自身业务与其他业务的解耦 
 *
 * @author xujh
 * @version 1.0
 * 2015年3月17日
 *
 */
public abstract class ASubject {
	
	//观察者对象容器
	protected final List<IObserver> obersvers = new ArrayList<IObserver>(10);
	
	/**
	 * 注册
	 *
	 * @param observer
	 */
	public void register(IObserver observer){
		obersvers.add(observer);
	}
	
	/**
	 * 注销
	 *
	 * @param observer
	 */
	public void destroy(IObserver observer){
		if(obersvers.contains(observer)){
			obersvers.remove(observer);
		}
	}
	
	/**
	 * 新增通知
	 *
	 * @param obj
	 */
	public void saveNotify(final Object obj){
		if(hasObservers()){
			getTaskExecutor().execute(new Runnable() {
				@Override
				public void run() {
					for(IObserver observer:obersvers){
						try {
							observer.execute4Save(obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
		}
	}
	
	/**
	 * 更新通知
	 *
	 * @param obj
	 */
	public void updateNotify(final Object obj){
		if(hasObservers()){
			getTaskExecutor().execute(new Runnable() {
				@Override
				public void run() {
					for(IObserver observer:obersvers){
						try {
							observer.execute4Update(obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	/**
	 * 删除通知
	 *
	 * @param obj
	 */
	public void deleteNotify(final Object obj){
		if(hasObservers()){
			getTaskExecutor().execute(new Runnable() {
				@Override
				public void run() {
					for(IObserver observer:obersvers){
						try {
							observer.execute4Delete(obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			
		}
	}

	private boolean hasObservers(){
		return this.obersvers!=null&&this.obersvers.size()>0;
	}

	public TaskExecutor getTaskExecutor() {
		return SpringContextHolder.getBean("taskExecutor");
	}
	
	
}
