package com.multithread;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThread {

	public static void main(final String[] args) {

		final Random random = new Random();

		final Lock resultsLock = new ReentrantLock();
		final Condition resultAvailableCondition = resultsLock.newCondition();

		final int producersNumber = 7;
		final int[] producerResults = new int[producersNumber];
		final ThreadGroup producersGroup = new ThreadGroup("Producers Group");
		for (int i = 0; i < producersNumber; i++) {
			final int producerIndex = i;
			new Thread(producersGroup, new Producer(next -> {
				resultsLock.lock();
				try {
					producerResults[producerIndex] = next;
					resultAvailableCondition.signalAll();
				} finally {
					resultsLock.unlock();
				}
			})).start();
		}

		final int consumersNumber = 2;
		final ThreadGroup consumersGroup = new ThreadGroup("Consumers Group");
		for (int i = 0; i < consumersNumber; i++) {
			new Thread(consumersGroup, () -> {
				int sum = 0;
				try {
					while (Thread.currentThread().isInterrupted() == false) {
						resultsLock.lock();
						resultAvailableCondition.await();
						try {
							sum += producerResults[random.nextInt(producersNumber)];
							if (sum >= 100) {
								System.out.println("I am winning! " + Thread.currentThread().getName());
								consumersGroup.interrupt();
								producersGroup.interrupt();
							}
						} finally {
							resultsLock.unlock();
						}
					}
				} catch (final Exception ex) {
				}
			}).start();
		}

	}
}
