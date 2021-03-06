/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.headwire.aem.tooling.intellij.io.eclipse;

import com.headwire.aem.tooling.intellij.eclipse.stub.IFile;
import com.headwire.aem.tooling.intellij.eclipse.stub.IFolder;
import com.headwire.aem.tooling.intellij.eclipse.stub.IResource;
import com.headwire.aem.tooling.intellij.util.Util;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.sling.ide.filter.Filter;
import org.apache.sling.ide.io.ConnectorException;
import org.apache.sling.ide.io.SlingProject;
import org.apache.sling.ide.io.SlingResource;
import org.apache.sling.ide.io.SlingResourceVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.headwire.aem.tooling.intellij.util.Constants.JCR_ROOT_FOLDER_NAME;

/**
 * Created by Andreas Schaefer (Headwire.com) on 11/15/15.
 */
public class SlingResource4Eclipse
    implements SlingResource
{
//AS TODO: This is an local only implementation -> Need to figure out how to handle remote part as well as remote only

    private SlingProject project;
    private IFile file;
    private IResource resource;
//    private VirtualFile file;
    // If there is no local file available the resource still could exist on the Sling Server
    private String resourcePath;

    public SlingResource4Eclipse(SlingProject project, IResource resource) {
        this.project = project;
        this.resource = resource;
    }

    public SlingResource4Eclipse(SlingProject project, String resourcePath) {
        this.project = project;
        this.resourcePath = resourcePath;
        // Try to find the local file
        SlingResource4Eclipse syncDirectory = (SlingResource4Eclipse) project.getSyncDirectory();
        this.resource = ((IFolder) syncDirectory.resource).findMember(resourcePath);
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public SlingProject getProject() {
        return project;
    }

    @Override
    public boolean isModified() {
        boolean ret = true;
        if(file != null) {
            //AS TODO: how to handle this in eclipse?
//            Long modificationTimestamp = Util.getModificationStamp(file);
//            Long fileModificationTimestamp = file.getModificationStamp();
//            ret = modificationTimestamp < fileModificationTimestamp;
        }
        return ret;
    }

    @Override
    public boolean isLocalOnly() {
        return false;
    }

    @Override
    public boolean isFolder() {
        //AS TODO: Handle the scenario when it is a Sling Server Resource only
        return resource == null ? false : resource instanceof IFolder;
    }

    @Override
    public boolean isFile() {
        //AS TODO: Handle the scenario when it is a Sling Server Resource only
        return resource == null ? false : resource instanceof IFile;
    }

    @Override
    public String getLocalPath() {
        return getLocalPath(true);
    }

    private String getLocalPath(boolean raw) {
        String ret = raw ?
            resource.getProjectRelativePath().toOSString():
            resource.getProjectRelativePath().toPortableString();
        return ret;
    }

    @Override
    public String getResourceLocalPath() {
        return getResourceLocalPath(true);
    }

    private String getResourceLocalPath(boolean raw) {
        String ret = getLocalPath(false);
        if(ret != null) {
            String syncDirectoryPath = ((SlingResource4Eclipse) project.getSyncDirectory()).getLocalPath(false);
            if (ret.startsWith(syncDirectoryPath)) {
                ret = ret.substring(syncDirectoryPath.length());
                if (!ret.startsWith("/")) {
                    ret = "/" + ret;
                }
            } else {
                throw new IllegalArgumentException("Resource Path: '" + ret + "' doest start with Sync Path: '" + syncDirectoryPath + "'");
            }
            if(!raw) {
                //AS TODO: Adjust this for Windows -> apply raw
            }
        }
        return ret;
    }

    @Override
    public String getResourcePath() {
        String ret = getResourceLocalPath(true);
        if(ret == null && resourcePath != null) {
            ret = resourcePath;
        }
        return ret;
    }

    @Override
    public InputStream getContentStream() throws IOException {
        return ((IFile) resource).getContents();
    }

    @Override
    public SlingResource getParent() {
        SlingResource ret = null;
        if(resource != null) {
            IResource parent = resource.getParent();
            if(!parent.getName().equals(JCR_ROOT_FOLDER_NAME)) {
                ret = new SlingResource4Eclipse(project, parent);
            }
        } else if(resourcePath != null) {
            String path = resourcePath;
            while(path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            if(path.length() > 0) {
                int index = path.lastIndexOf('/');
                if(index > 0) {
                    String parentResourcePath = path.substring(0, index);
                    ret = new SlingResource4Eclipse(project, parentResourcePath);
                }
            }
        }
        return ret;
    }

    @Override
    public SlingResource findInParentByName(String name) {
        SlingResource ret = null;
        SlingResource parent = this;
        if(parent.getName().equals(name)) {
            ret = parent;
        } else {
            while ((parent = parent.getParent()) != null) {
                if (parent.getName().equals(name)) {
                    ret = parent;
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public SlingResource findChild(String childFileName) {
        SlingResource ret = null;
        for(IResource child: resource.members()) {
            if(child.getName().equals(childFileName)) {
                ret = new SlingResource4Eclipse(project, child);
            }
        }
        if(ret == null) {
            ret = new SlingResource4Eclipse(project, getResourcePath() + "/" + childFileName);
        }
        return ret;
    }

    @Override
    public SlingResource findChildByPath(String childPath) {
        SlingResource ret = null;
        // Remove trailing slashes
        if(childPath.startsWith("/")) {
            childPath = childPath.substring(1);
        }
        if(isFolder()) {
            IFolder folder = (IFolder) resource;
            IResource child = folder.findMember(childPath);
            if (child != null) {
                ret = new SlingResource4Eclipse(project, child);
            } else {
                ret = new SlingResource4Eclipse(project, childPath);
            }
        }
        return ret;
    }

    @Override
    public List<SlingResource> getLocalChildren() {
        List<SlingResource> ret = new ArrayList<SlingResource>();
        for(IResource child: resource.members()) {
            ret.add(new SlingResource4Eclipse(project, child));
        }
        return ret;
    }

    @Override
    public boolean existsLocally() {
        return isFile();
    }

    @Override
    public boolean existsRemotely() {
        //AS TODO: How to check that? Probably need to defer that until we tested it and then set it
        return false;
    }

    @Override
    public void accept(SlingResourceVisitor visitor) throws ConnectorException {
//        accept(visitor, );
    }

    @Override
    public void accept(SlingResourceVisitor visitor, int depth, int memberFlags) throws ConnectorException {

    }

    @Override
    public SlingResource getSyncDirectory() {
        return getProject().getSyncDirectory();
    }

    @Override
    public SlingResource getResourceFromPath(String resourcePath) {
        return new SlingResource4Eclipse(project, resourcePath);
    }

    @Override
    public Filter loadFilter() throws ConnectorException {
        return getProject().loadFilter();
    }

    @Override
    /**
     * Check if the two resource are equal by checking if both have the same file (if set) or the same resource path
     */
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        SlingResource4Eclipse that = (SlingResource4Eclipse) o;

        if(file != null) {
            return file.equals(that.file);
        } else {
            return
                resourcePath != null ?
                resourcePath.equals(that.resourcePath) :
                that.resourcePath == null;
        }
    }

    @Override
    public int hashCode() {
        int result = file != null ? file.hashCode() : 0;
        result = 31 * result + (getResourcePath() != null ? getResourcePath().hashCode() : 0);
        return result;
    }
}
