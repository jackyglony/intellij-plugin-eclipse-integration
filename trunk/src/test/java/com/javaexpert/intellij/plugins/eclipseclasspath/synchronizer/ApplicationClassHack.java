package com.javaexpert.intellij.plugins.eclipseclasspath.synchronizer;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.BaseComponent;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.picocontainer.PicoContainer;

import java.awt.*;

/**
 * User: piotrga
 * Date: 2007-03-18
 * Time: 13:45:51
 */
public class ApplicationClassHack implements Application {
    public void runReadAction(Runnable action) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T runReadAction(Computable<T> computation) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void runWriteAction(Runnable action) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T runWriteAction(Computable<T> computation) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Nullable
    public Object getCurrentWriteAction(Class actionClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void assertReadAccessAllowed() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void assertWriteAccessAllowed() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void assertIsDispatchThread() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addApplicationListener(ApplicationListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeApplicationListener(ApplicationListener listener) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveAll() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void saveSettings() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void exit() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isWriteAccessAllowed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isReadAccessAllowed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isDispatchThread() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void invokeLater(Runnable runnable) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void invokeLater(Runnable runnable, @NotNull ModalityState state) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void invokeAndWait(Runnable runnable, @NotNull ModalityState modalityState) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public ModalityState getCurrentModalityState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ModalityState getModalityStateForComponent(Component c) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ModalityState getDefaultModalityState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public ModalityState getNoneModalityState() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getStartTime() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getIdleTime() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isUnitTestMode() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isHeadlessEnvironment() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IdeaPluginDescriptor getPlugin(PluginId id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public IdeaPluginDescriptor[] getPlugins() {
        return new IdeaPluginDescriptor[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BaseComponent getComponent(String name) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T getComponent(Class<T> interfaceClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T getComponent(Class<T> interfaceClass, T defaultImplementationIfAbsent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public Class[] getComponentInterfaces() {
        return new Class[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean hasComponent(@NotNull Class interfaceClass) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public <T> T[] getComponents(Class<T> baseInterfaceClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public PicoContainer getPicoContainer() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isDisposed() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> T getUserData(Key<T> key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> void putUserData(Key<T> key, T value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
