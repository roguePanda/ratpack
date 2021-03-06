/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ratpack.groovy.handling.internal;

import com.google.common.reflect.TypeToken;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import ratpack.api.NonBlocking;
import ratpack.api.Nullable;
import ratpack.exec.ExecController;
import ratpack.exec.ExecInterceptor;
import ratpack.exec.Fulfiller;
import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.groovy.handling.GroovyByContentHandler;
import ratpack.groovy.handling.GroovyByMethodHandler;
import ratpack.groovy.handling.GroovyContext;
import ratpack.groovy.internal.ClosureUtil;
import ratpack.handling.*;
import ratpack.handling.direct.DirectChannelAccess;
import ratpack.http.Request;
import ratpack.http.Response;
import ratpack.http.client.HttpClient;
import ratpack.launch.LaunchConfig;
import ratpack.parse.NoSuchParserException;
import ratpack.parse.Parse;
import ratpack.parse.ParserException;
import ratpack.path.PathTokens;
import ratpack.registry.NotInRegistryException;
import ratpack.registry.Registry;
import ratpack.server.BindAddress;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

public class DefaultGroovyContext implements GroovyContext {

  private final Context delegate;

  public DefaultGroovyContext(Context delegate) {
    this.delegate = delegate;
  }

  @Override
  public GroovyContext getContext() {
    return this;
  }

  @Override
  public Supplier getSupplier() {
    return delegate.getSupplier();
  }

  @Override
  public LaunchConfig getLaunchConfig() {
    return delegate.getLaunchConfig();
  }

  @Override
  public DirectChannelAccess getDirectChannelAccess() {
    return delegate.getDirectChannelAccess();
  }

  @Override
  public void addExecInterceptor(ExecInterceptor execInterceptor, Action<? super Context> action) throws Exception {
    delegate.addExecInterceptor(execInterceptor, action);
  }

  @Override
  public List<ExecInterceptor> getInterceptors() {
    return delegate.getInterceptors();
  }

  @Override
  public void byMethod(Closure<?> closure) {
    ByMethodHandler handler = getByMethod();
    GroovyByMethodHandler groovyHandler = new DefaultGroovyByMethodHandler(this, handler);
    ClosureUtil.configureDelegateFirst(groovyHandler, closure);
    try {
      groovyHandler.handle(this);
    } catch (Exception e) {
      delegate.error(e);
    }
  }

  @Override
  public void byContent(@DelegatesTo(ByMethodHandler.class) Closure<?> closure) {
    ByContentHandler handler = getByContent();
    GroovyByContentHandler groovyHandler = new DefaultGroovyByContentHandler(this, handler);
    ClosureUtil.configureDelegateFirst(groovyHandler, closure);
    try {
      groovyHandler.handle(this);
    } catch (Exception e) {
      delegate.error(e);
    }
  }

  @Override
  public void onClose(Closure<?> callback) {
    onClose(ClosureUtil.delegatingAction(callback));
  }

  @Override
  public Request getRequest() {
    return delegate.getRequest();
  }

  @Override
  public Response getResponse() {
    return delegate.getResponse();
  }

  @Override
  @NonBlocking
  public void next() {
    delegate.next();
  }

  @Override
  @NonBlocking
  public void next(Registry registry) {
    delegate.next(registry);
  }

  @Override
  @NonBlocking
  public void insert(Handler... handlers) {
    delegate.insert(handlers);
  }

  @Override
  @NonBlocking
  public void insert(Registry registry, Handler... handlers) {
    delegate.insert(registry, handlers);
  }

  @Override
  @NonBlocking
  public void respond(Handler handler) {
    delegate.respond(handler);
  }

  @Override
  public ByMethodHandler getByMethod() {
    return delegate.getByMethod();
  }

  @Override
  public ByContentHandler getByContent() {
    return delegate.getByContent();
  }

  @Override
  @NonBlocking
  public void error(Exception exception) throws NotInRegistryException {
    delegate.error(exception);
  }

  @Override
  @NonBlocking
  public void clientError(int statusCode) throws NotInRegistryException {
    delegate.clientError(statusCode);
  }

  @Override
  public PathTokens getPathTokens() throws NotInRegistryException {
    return delegate.getPathTokens();
  }

  @Override
  public PathTokens getAllPathTokens() throws NotInRegistryException {
    return delegate.getAllPathTokens();
  }

  @Override
  public Path file(String path) throws NotInRegistryException {
    return delegate.file(path);
  }

  @Override
  @NonBlocking
  public void render(Object object) {
    delegate.render(object);
  }

  @Override
  public ExecController getExecController() {
    return delegate.getExecController();
  }

  @Override
  public <T> Promise<T> blocking(Callable<T> blockingOperation) {
    return delegate.blocking(blockingOperation);
  }

  @Override
  public <T> Promise<T> promise(Action<? super Fulfiller<T>> action) {
    return delegate.promise(action);
  }

  @Override
  public void redirect(String location) throws NotInRegistryException {
    delegate.redirect(location);
  }

  @Override
  public void redirect(int code, String location) throws NotInRegistryException {
    delegate.redirect(code, location);
  }

  @Override
  @NonBlocking
  public void lastModified(Date date, Runnable runnable) {
    delegate.lastModified(date, runnable);
  }

  @Override
  public BindAddress getBindAddress() {
    return delegate.getBindAddress();
  }

  @Override
  public <T> T parse(Class<T> type) throws NoSuchParserException, ParserException {
    return delegate.parse(type);
  }

  @Override
  public <T, O> T parse(Class<T> type, O options) throws NoSuchParserException, ParserException {
    return delegate.parse(type, options);
  }

  @Override
  public <T, O> T parse(Parse<T, O> parse) {
    return delegate.parse(parse);
  }

  @Override
  public void onClose(Action<? super RequestOutcome> callback) {
    delegate.onClose(callback);
  }

  @Override
  public <O> O get(Class<O> type) throws NotInRegistryException {
    return delegate.get(type);
  }

  @Override
  @Nullable
  public <O> O maybeGet(Class<O> type) {
    return delegate.maybeGet(type);
  }

  @Override
  public <O> List<O> getAll(Class<O> type) {
    return delegate.getAll(type);
  }

  @Override
  public HttpClient getHttpClient() {
    return delegate.getHttpClient();
  }

  @Override
  public <O> O get(TypeToken<O> type) throws NotInRegistryException {
    return delegate.get(type);
  }

  @Override
  @Nullable
  public <O> O maybeGet(TypeToken<O> type) {
    return delegate.maybeGet(type);
  }

  @Override
  public <O> List<O> getAll(TypeToken<O> type) {
    return delegate.getAll(type);
  }
}
