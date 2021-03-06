package nl.kii.util

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import rx.Observable
import rx.Observer
import rx.Scheduler
import rx.subjects.AsyncSubject
import rx.subjects.PublishSubject

class RxExtensions {

	// Creating Streams

	def static <T> PublishSubject<T> newStream() {
		PublishSubject.create
	}

	def static <T> PublishSubject<T> stream(Class<T> type) {
		PublishSubject.create
	}

	def static <T> PublishSubject<T> stream(T instance) {
		PublishSubject.create
	}

	def static <T> Observable<T> stream(Iterable<? extends T> iterable) {
		Observable.from(iterable)
	}
	
	/** Create a new Observable from the passed Observable (stream). Internally it creates a 
	 * ConnectableObservable<T>, connects it, and returns it.
	 * <p>
	 * This allows you to split a stream into multiple streams:
	 * <pre>
	 * stream => [
	 * 		split.each [ println('1st stream got ' + it) ]
	 * 		split.each [ println('2nd stream also got ' + it) ]
	 * ]
	 * </pre> 
	 */
	def static <T> split(Observable<T> stream) {
		val connector = stream.publish
		connector.connect
		connector
		// alternative implementation: newStream => [ stream.each(it) ]
	}

	// Creating Promises
	
	def static <T> AsyncSubject<T> newPromise() {
		AsyncSubject.create
	}

	def static <T> AsyncSubject<T> promise(Class<T> type) {
		AsyncSubject.create
	}
	
	def static <T> Observable<T> promise(T item) {
		Observable.from(item)
	}

	def static <T> Observable<T> promise(T item, Observer<T> observer) {
		item.promise.each(observer)
	}

	def static <T> Observable<T> promise(Future<?> future, Class<T> type) {
		Observable.from(future as Future<T>)
	}

	def static <T> Observable<T> promise(Future<? extends T> future, Scheduler scheduler) {
		Observable.from(future, scheduler)
	}

	def static <T> Observable<T> promise(Future<? extends T> future, long timeout, TimeUnit timeUnit) {
		Observable.from(future, timeout, timeUnit)
	}
	
	// Sending data to a Stream

	def static<T> apply(Observer<T> stream, T value) {
		stream.onNext(value)
		stream
	}

//	def static<T> apply(PublishSubject<T> stream, T value) {
//		stream.onNext(value)
//		stream
//	}
//	
	def static<T> complete(PublishSubject<T> subject) {
		subject.onCompleted
	}
	
	def static <T> Iterable<? extends T> each(Iterable<? extends T> iterable, Observer<T> observer) {
		iterable.forEach [ observer.onNext(it) ]
		iterable
	}
	
	// Sending data to a Promise
	
	def static<T> apply(AsyncSubject<T> subject, T value) {
		subject.onNext(value)
		subject.onCompleted
		subject
	}
	
	// Collecting results
	
	def static <T> each(Observable<T> stream, (T)=>void handler) {
		stream.subscribe(handler)
		stream
	}

	def static <T> each(Observable<T> stream, Observer<T> observer) {
		stream.subscribe(observer)
		stream
	}

	def static <T> onError(Observable<T> stream, (Throwable)=>void handler) {
		stream.subscribe(new ObserverHelper<T>([], handler, [|]))
		stream
	}
	
	def static <T> onFinish(Observable<T> stream, (Object)=>void handler) {
		stream.subscribe(new ObserverHelper<T>([], [], [| handler.apply(null) ]))
		stream
	}
	
}

class ObserverHelper<T> implements Observer<T> {
	
	val (T)=>void onNext
	val (Throwable)=>void onError
	val ()=>void onCompleted
	
	new((T)=>void onNext, (Throwable)=>void onError, ()=>void onCompleted) {
		this.onNext = onNext
		this.onError = onError
		this.onCompleted = onCompleted
	}
	
	override onCompleted() {
		onCompleted.apply
	}
	
	override onError(Throwable e) {
		onError.apply(e)
	}
	
	override onNext(T v) {
		onNext.apply(v)
	}
	
}
