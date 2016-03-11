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

package com.headwire.aem.tooling.intellij.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenResource;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schaefa on 7/2/15.
 */
public class ModuleProjectImpl
    implements ModuleProject
{
    private MavenProject mavenProject;

    public ModuleProjectImpl(@NotNull MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    @Override
    public String getGroupId() {
        return mavenProject.getMavenId().getGroupId();
    }

    @Override
    public String getArtifactId() {
        return mavenProject.getMavenId().getArtifactId();
    }

    @Override
    public String getVersion() {
        return mavenProject.getMavenId().getVersion();
    }

    @Override
    public String getName() {
        return mavenProject.getName();
    }

    @Override
    public String getBuildFileName() {
        return mavenProject.getFinalName();
    }

    @Override
    public boolean isOSGiBundle() {
        return mavenProject.getPackaging().equalsIgnoreCase("bundle");
    }

    @Override
    public boolean isContent() {
        return mavenProject.getPackaging().equalsIgnoreCase("content-package");
    }

    @Override
    public String getBuildDirectoryPath() {
        return mavenProject.getBuildDirectory();
    }

    @Override
    public String getModuleDirectory() {
        return mavenProject.getDirectoryFile().getPath();
    }

    @Override
    public List<String> getContentDirectoryPaths() {
        List<MavenResource> resourceList = mavenProject.getResources();
        List<String> ret = new ArrayList<String>(resourceList.size());
        for(MavenResource resource: resourceList) {
            ret.add(resource.getDirectory());
        }
        return ret;
    }
}