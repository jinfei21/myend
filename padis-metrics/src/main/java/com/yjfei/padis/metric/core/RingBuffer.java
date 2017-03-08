package com.yjfei.padis.metric.core;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Lists;
import com.yjfei.padis.util.SleepUtils;

public class RingBuffer<E> {
	private AtomicLong takeIndex = new AtomicLong(-1);
	private AtomicLong putIndex = new AtomicLong(-1);
	private int bufferSize;
	private E[] entries;

	@SuppressWarnings("unchecked")
	public RingBuffer(int bufferSize) {
		this.bufferSize = bufferSize;
		entries = (E[]) new Object[this.bufferSize];
	}

	public E getElement(int index) {
		return elementAt(index);
	}

	protected final E elementAt(int index) {
		E e = entries[index];
		return e;
	}

	public int getNextSlot(AtomicLong currentIndex) {
		return getNextSlot(currentIndex, 1);
	}

	public int getNextSlot(AtomicLong currentIndex, long n) {
		return (int) (currentIndex.addAndGet(n) % bufferSize);

	}

	public boolean put(E e) {
		if (putIndex.get() - takeIndex.get() > bufferSize) {
			return true;
		}
		int next = getNextSlot(putIndex);
		entries[next] = e;
		return false;
	}

	public void put(List<E> list) {
		int length = list.size();
		for (int i = 0; i < length; i++) {
			put(list.get(i));
		}
	}

	public void putForWait(E e) {
		int next = getNextSlot(putIndex);
		while (putIndex.get() - takeIndex.get() > bufferSize) {
			SleepUtils.sleep(50);
		}
		entries[next] = e;
	}

	public void put(E e, long timeout) {
		int next = getNextSlot(putIndex);
		while (putIndex.get() - takeIndex.get() > bufferSize) {
			
			SleepUtils.sleep(50);
			if (putIndex.get() - takeIndex.get() > bufferSize) {
				break;
			}
			
		}
		entries[next] = e;
	}

	public E takeNextForWait() {
		long nextTake = takeIndex.incrementAndGet();

		while (nextTake >= putIndex.get()) {
			SleepUtils.sleep(50);
		}
		return entries[getCurrSlot(takeIndex)];
	}

	public E takeNextForTimeout(long timeout) {
		long nextTake = takeIndex.incrementAndGet();
		
		while (nextTake >= putIndex.get()) {
			SleepUtils.sleep(timeout);
			if (nextTake >= putIndex.get()) {
				takeIndex.decrementAndGet();
				return null;
			}

		}
		return entries[getCurrSlot(takeIndex)];
	}

	private int getCurrSlot(AtomicLong currIndex) {
		return (int) (currIndex.get() % bufferSize);
	}

	public List<E> takeForWait(int n) {
		List<E> list = Lists.newArrayList();
		for (int i = 0; i < n; i++) {
			E e = takeNextForWait();
			if (null == e) {
				continue;
			}
			list.add(e);
		}
		return list;
	}

	public List<E> takeForTimeout(int n, long timeout) {
		List<E> list = Lists.newArrayList();
		long singleTimeout = timeout / n;
		for (int i = 0; i < n; i++) {
			E e = takeNextForTimeout(singleTimeout);
			if (null == e) {
				continue;
			}
			list.add(e);
		}
		return list;
	}

	public int getBufferSize() {
		return bufferSize;
	}

}
