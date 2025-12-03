/*
 * Copyright 2024-2025 NetCracker Technology Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.qubership.itool.modules.graph;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphClassifierBuilderImpl implements GraphClassifierBuilder {

    private String id;
    private boolean withReport = true;
    private List<String> departmentIds = new ArrayList<>();
    private List<String> domainIds = new ArrayList<>();
    private List<String> releaseVersionIds = new ArrayList<>();
    private List<String> applicationVersionIds = new ArrayList<>();

    public GraphClassifierBuilderImpl() {

    }

    @Override
    public GraphClassifierBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public GraphClassifierBuilder setWithReport(boolean withReport) {
        this.withReport = withReport;
        return this;
    }

    @Override
    public GraphClassifierBuilder setDepartmentIds(List<String> departmentIds) {
        this.departmentIds = departmentIds;
        return this;
    }

    @Override
    public GraphClassifierBuilder setDomainIds(List<String> domainIds) {
        this.domainIds = domainIds;
        return this;
    }

    @Override
    public GraphClassifierBuilder setReleaseVersionIds(List<String> releaseVersionIds) {
        this.releaseVersionIds = releaseVersionIds;
        return this;
    }

    @Override
    public GraphClassifierBuilder setApplicationVersionIds(List<String> applicationVersionIds) {
        this.applicationVersionIds = applicationVersionIds;
        return this;
    }

    public GraphClassifierBuilder addDepartment(String departmentId) {
        this.departmentIds.add(departmentId);
        return this;
    }

    @Override
    public GraphClassifierBuilder addDomain(String domainId) {
        this.domainIds.add(domainId);
        return this;
    }

    @Override
    public GraphClassifierBuilder addReleaseVersionId(String releaseVersionId) {
        this.releaseVersionIds.add(releaseVersionId);
        return this;
    }

    @Override
    public GraphClassifierBuilder addApplicationVersionId(String applicationVersionId) {
        this.applicationVersionIds.add(applicationVersionId);
        return this;
    }

    public GraphClassifier build() {
        return new GraphClassifierImpl(
                (this.id == null ? generateMD5Checksum() : this.id),
                this.withReport,
                this.departmentIds,
                this.domainIds,
                this.releaseVersionIds,
                this.applicationVersionIds
        );
    }

    private String generateMD5Checksum() {
        // Sort lists to ensure deterministic hash generation regardless of insertion order
        List<String> sortedDepartmentIds = new ArrayList<>(this.departmentIds);
        List<String> sortedDomainIds = new ArrayList<>(this.domainIds);
        List<String> sortedReleaseVersionIds = new ArrayList<>(this.releaseVersionIds);
        List<String> sortedApplicationVersionIds = new ArrayList<>(this.applicationVersionIds);

        Collections.sort(sortedDepartmentIds);
        Collections.sort(sortedDomainIds);
        Collections.sort(sortedReleaseVersionIds);
        Collections.sort(sortedApplicationVersionIds);

        StringBuilder source = new StringBuilder();
        source.append("departmentIds: ").append(sortedDepartmentIds);
        source.append(", domainsIds: ").append(sortedDomainIds);
        source.append(", releaseVersionIds: ").append(sortedReleaseVersionIds);
        source.append(", applicationVersionIds: ").append(sortedApplicationVersionIds);
        String md5 = DigestUtils.md5Hex(source.toString());
        return md5;
    }

}
