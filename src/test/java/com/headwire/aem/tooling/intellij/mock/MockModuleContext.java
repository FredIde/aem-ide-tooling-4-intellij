/*
 *
 *  * Licensed to the Apache Software Foundation (ASF) under one or more
 *  * contributor license agreements.  See the NOTICE file distributed with
 *  * this work for additional information regarding copyright ownership.
 *  * The ASF licenses this file to You under the Apache License, Version 2.0
 *  * (the "License"); you may not use this file except in compliance with
 *  * the License.  You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.headwire.aem.tooling.intellij.mock;

import com.headwire.aem.tooling.intellij.config.ModuleContext;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Andreas Schaefer (Headwire.com) on 12/28/15.
 */
public class MockModuleContext
    implements ModuleContext
{
    public enum Type{ OSGI, Content, Other };

    private String symbolicName = "testGroup.testArtifact";
    private String version = "1.0";
    private String name = "testName";
    private String buildFileName = "testBuildFileName";
    private Type type = Type.OSGI;
    private String buildDirectoryPath = "/build";
    private String moduleDirectoryPath = "/src";
    private List<String> contentDirectoryPaths = Arrays.asList();
    private String metainfPath = "";

    @Override
    public void init(@NotNull Object payload) {
    }

    @Override
    public String getSymbolicName() {
        return symbolicName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBuildFileName() {
        return buildFileName;
    }

    @Override
    public boolean isOSGiBundle() {
        return type == Type.OSGI;
    }

    @Override
    public boolean isContent() {
        return type == Type.Content;
    }

    @Override
    public String getBuildDirectoryPath() {
        return buildDirectoryPath;
    }

    @Override
    public String getModuleDirectory() {
        return moduleDirectoryPath;
    }

    @Override
    public List<String> getContentDirectoryPaths() {
        return contentDirectoryPaths;
    }

    @Override
    public String getMetaInfPath() {
        return metainfPath;
    }

    public MockModuleContext setSymbolicName(String symbolicName) {
        this.symbolicName = symbolicName;
        return this;
    }

    public MockModuleContext setVersion(String version) {
        this.version = version;
        return this;
    }

    public MockModuleContext setName(String name) {
        this.name = name;
        return this;
    }

    public MockModuleContext setBuildFileName(String buildFileName) {
        this.buildFileName = buildFileName;
        return this;
    }

    public MockModuleContext setType(Type type) {
        this.type = type;
        return this;
    }

    public MockModuleContext setBuildDirectoryPath(String buildDirectoryPath) {
        this.buildDirectoryPath = buildDirectoryPath;
        return this;
    }

    public MockModuleContext setModuleDirectoryPath(String moduleDirectoryPath) {
        this.moduleDirectoryPath = moduleDirectoryPath;
        return this;
    }

    public MockModuleContext setContentDirectoryPaths(List<String> contentDirectoryPaths) {
        this.contentDirectoryPaths = contentDirectoryPaths;
        return this;
    }

    public MockModuleContext setMetainfPath(String metainfPath) {
        this.metainfPath = metainfPath;
        return this;
    }

    @Override
    public boolean isMavenBased() {
        return false;
    }

    @Override
    public Module getModule() {
        return null;
    }
}