package nl.kii.util;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import nl.kii.util.Collector;
import nl.kii.util.Countdown;
import nl.kii.util.RxExtensions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Pair;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.Test;
import rx.Observable;
import rx.subjects.AsyncSubject;
import rx.subjects.PublishSubject;
import rx.util.functions.Func1;

@SuppressWarnings("all")
public class TestRXExtensions {
  @Test
  public void testRXStream() {
    final PublishSubject<Integer> stream = RxExtensions.<Integer>stream(Integer.class);
    Observable<Integer> _take = stream.take(3);
    final Func1<Integer,String> _function = new Func1<Integer,String>() {
        public String call(final Integer it) {
          String _plus = ("got number " + it);
          return _plus;
        }
      };
    Observable<String> _map = _take.<String>map(_function);
    final Procedure1<String> _function_1 = new Procedure1<String>() {
        public void apply(final String it) {
          String _plus = ("a: " + it);
          InputOutput.<String>println(_plus);
        }
      };
    Observable<String> _each = RxExtensions.<String>each(_map, _function_1);
    final Procedure1<Object> _function_2 = new Procedure1<Object>() {
        public void apply(final Object it) {
          InputOutput.<String>println("we are done!");
        }
      };
    Observable<String> _onFinish = RxExtensions.<String>onFinish(_each, _function_2);
    final Procedure1<Throwable> _function_3 = new Procedure1<Throwable>() {
        public void apply(final Throwable it) {
          String _plus = ("caught: " + it);
          InputOutput.<String>println(_plus);
        }
      };
    RxExtensions.<String>onError(_onFinish, _function_3);
    final Procedure1<PublishSubject<Integer>> _function_4 = new Procedure1<PublishSubject<Integer>>() {
        public void apply(final PublishSubject<Integer> it) {
          RxExtensions.<Integer>apply(it, Integer.valueOf(2));
          RxExtensions.<Integer>apply(it, Integer.valueOf(5));
          RxExtensions.<Integer>apply(it, Integer.valueOf(3));
          RxExtensions.<Integer>complete(it);
        }
      };
    ObjectExtensions.<PublishSubject<Integer>>operator_doubleArrow(stream, _function_4);
  }
  
  @Test
  public void testRXPromise() {
    final AsyncSubject<String> promise = RxExtensions.<String>promise(String.class);
    final Func1<String,String> _function = new Func1<String,String>() {
        public String call(final String it) {
          String _lowerCase = it.toLowerCase();
          return _lowerCase;
        }
      };
    Observable<String> _map = promise.<String>map(_function);
    final Func1<String,String> _function_1 = new Func1<String,String>() {
        public String call(final String it) {
          String _plus = ("Hey cool I got this text: " + it);
          return _plus;
        }
      };
    Observable<String> _map_1 = _map.<String>map(_function_1);
    final Procedure1<String> _function_2 = new Procedure1<String>() {
        public void apply(final String it) {
          InputOutput.<String>println(it);
        }
      };
    Observable<String> _each = RxExtensions.<String>each(_map_1, _function_2);
    final Procedure1<Object> _function_3 = new Procedure1<Object>() {
        public void apply(final Object it) {
          InputOutput.<String>println("we are done!");
        }
      };
    Observable<String> _onFinish = RxExtensions.<String>onFinish(_each, _function_3);
    final Procedure1<Throwable> _function_4 = new Procedure1<Throwable>() {
        public void apply(final Throwable it) {
          String _plus = ("caught: " + it);
          InputOutput.<String>println(_plus);
        }
      };
    RxExtensions.<String>onError(_onFinish, _function_4);
    RxExtensions.<String>apply(promise, "Hello!");
  }
  
  @Test
  public void testConnectedStream() {
    RxExtensions.<Integer>each(Collections.<Integer>unmodifiableList(Lists.<Integer>newArrayList(1, 2, 3)), this.handler);
  }
  
  private final PublishSubject<Integer> handler = new Function0<PublishSubject<Integer>>() {
    public PublishSubject<Integer> apply() {
      PublishSubject<Integer> _stream = RxExtensions.<Integer>stream(Integer.class);
      final Procedure1<PublishSubject<Integer>> _function = new Procedure1<PublishSubject<Integer>>() {
          public void apply(final PublishSubject<Integer> it) {
            final Procedure1<Integer> _function = new Procedure1<Integer>() {
                public void apply(final Integer it) {
                  String _plus = ("handler got number: " + it);
                  InputOutput.<String>println(_plus);
                }
              };
            RxExtensions.<Integer>each(it, _function);
          }
        };
      PublishSubject<Integer> _doubleArrow = ObjectExtensions.<PublishSubject<Integer>>operator_doubleArrow(_stream, _function);
      return _doubleArrow;
    }
  }.apply();
  
  @Test
  public void testCountDown() {
    Countdown _countdown = new Countdown();
    final Countdown countdown = _countdown;
    final Procedure1<? super Boolean> c1 = countdown.await();
    final Procedure1<? super Boolean> c2 = countdown.await();
    final Procedure1<? super Boolean> c3 = countdown.await();
    Observable<Pair<String,Boolean>> _stream = countdown.stream();
    final Procedure1<Object> _function = new Procedure1<Object>() {
        public void apply(final Object it) {
          Boolean _isSuccess = countdown.isSuccess();
          String _plus = ("countdown done. success:" + _isSuccess);
          InputOutput.<String>println(_plus);
        }
      };
    RxExtensions.<Pair<String,Boolean>>onFinish(_stream, _function);
    c2.apply(Boolean.valueOf(true));
    c1.apply(Boolean.valueOf(true));
    c3.apply(Boolean.valueOf(true));
  }
  
  @Test
  public void testCollector() {
    Collector<String> _collector = new Collector<String>();
    final Collector<String> collector = _collector;
    final Procedure1<? super String> cuser = collector.await("user");
    final Procedure1<? super String> cname = collector.await("name");
    final Procedure1<? super String> cage = collector.await("age");
    Observable<Pair<String,String>> _stream = collector.stream();
    final Procedure1<Pair<String,String>> _function = new Procedure1<Pair<String,String>>() {
        public void apply(final Pair<String,String> it) {
          String _key = it.getKey();
          String _plus = ("got " + _key);
          String _plus_1 = (_plus + " has value ");
          String _value = it.getValue();
          String _plus_2 = (_plus_1 + _value);
          InputOutput.<String>println(_plus_2);
        }
      };
    RxExtensions.<Pair<String,String>>each(_stream, _function);
    Observable<Pair<String,String>> _stream_1 = collector.stream();
    final Procedure1<Object> _function_1 = new Procedure1<Object>() {
        public void apply(final Object it) {
          final ConcurrentHashMap<String,String> it_1 = collector.result();
          String _get = it_1.get("user");
          String _plus = ("found user " + _get);
          InputOutput.<String>println(_plus);
          String _get_1 = it_1.get("name");
          String _plus_1 = ("found name " + _get_1);
          InputOutput.<String>println(_plus_1);
          String _get_2 = it_1.get("age");
          String _plus_2 = ("found age " + _get_2);
          InputOutput.<String>println(_plus_2);
        }
      };
    RxExtensions.<Pair<String,String>>onFinish(_stream_1, _function_1);
    cage.apply("12");
    cname.apply("John");
    cuser.apply("Christian");
  }
  
  @Test
  public void testConnectables() {
    final PublishSubject<Integer> stream = RxExtensions.<Integer>stream(Integer.class);
    PublishSubject<PublishSubject<Integer>> _stream = RxExtensions.<PublishSubject<Integer>>stream(stream);
    final Procedure1<PublishSubject<Integer>> _function = new Procedure1<PublishSubject<Integer>>() {
        public void apply(final PublishSubject<Integer> it) {
          String _plus = ("a: " + it);
          InputOutput.<String>println(_plus);
        }
      };
    RxExtensions.<PublishSubject<Integer>>each(_stream, _function);
    PublishSubject<PublishSubject<Integer>> _stream_1 = RxExtensions.<PublishSubject<Integer>>stream(stream);
    final Func1<PublishSubject<Integer>,String> _function_1 = new Func1<PublishSubject<Integer>,String>() {
        public String call(final PublishSubject<Integer> it) {
          String _plus = ("got value " + it);
          return _plus;
        }
      };
    Observable<String> _map = _stream_1.<String>map(_function_1);
    final Procedure1<String> _function_2 = new Procedure1<String>() {
        public void apply(final String it) {
          String _plus = ("b: " + it);
          InputOutput.<String>println(_plus);
        }
      };
    RxExtensions.<String>each(_map, _function_2);
    RxExtensions.<Integer>apply(stream, Integer.valueOf(2));
  }
}
