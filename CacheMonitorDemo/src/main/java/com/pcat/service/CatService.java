package com.pcat.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;
import org.unidal.helper.Threads;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.DefaultMessageProducer;


@Service
public class CatService {

	private static ExecutorService threadPool = Threads.forPool().getFixedThreadPool("Test", 100);
	
	public String syn(){
		Transaction tran1 = Cat.newTransaction("CatService", "remoteSyn1");
		
		try{
			Thread.sleep(20);
			tran1.setStatus(Transaction.SUCCESS);
		}catch(Throwable t){
			Cat.logError(t);
		}finally{
			tran1.complete();
		}
		
		Transaction tran2 = Cat.newTransaction("CatService", "remoteSyn2");
		try{
			Thread.sleep(20);
			tran1.setStatus(Transaction.SUCCESS);
		}catch(Throwable t){
			tran2.setStatus(t);
			Cat.logError(t);
		}finally{
			tran2.complete();
		}
		return null;
	}
	
	
	public void asy(){
		final Transaction tran1 = Cat.newTransaction("CatService", "remoteAsy");
		
		try{
			Thread.sleep(8);
			
			final Semaphore semaphone = new Semaphore(0);
			final AtomicInteger count = new AtomicInteger(0);
			for(int i = 0;i < 2;i++){
				threadPool.submit(new Runnable(){
	
					@Override
					public void run() {
						Transaction tran = ((DefaultMessageProducer)Cat.getProducer()).newTransaction(tran1,"CatService", "remoteAsy"+count.incrementAndGet());
						try{
							Thread.sleep(10);
							tran.setStatus(Transaction.SUCCESS);
						}catch(Throwable t){
							tran.setStatus(t);
						}finally{
							tran.complete();
							semaphone.release();
						}
						
					}
					
				});
			}
			tran1.setStatus(Transaction.SUCCESS);
			try{
				semaphone.tryAcquire(2, 10000, TimeUnit.MILLISECONDS);
			}catch(InterruptedException e){
				tran1.setStatus(e);
			}
			
		}catch(Throwable t){
			Cat.logError(t);
		}finally{
			tran1.complete();
		}
	}
}
