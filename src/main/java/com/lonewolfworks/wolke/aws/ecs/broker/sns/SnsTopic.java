/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lonewolfworks.wolke.aws.ecs.broker.sns;

import java.util.List;
import java.util.StringJoiner;

public class SnsTopic {

    private String name;
    private String policyName;
    private Boolean autoRemoveSubscriptions;
    private Boolean serverSideEncryption = false;
    private String kmsMasterKeyId;
    private List<SnsSubscription> subscriptions;

    private List<SnsTopicDeliveryStatusAttribute> deliveryStatusAttributes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolicyName() {
        return policyName;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public List<SnsSubscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<SnsSubscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Boolean getAutoRemoveSubscriptions() {
        return autoRemoveSubscriptions;
    }

    public void setAutoRemoveSubscriptions(Boolean autoRemoveSubscriptions) {
        this.autoRemoveSubscriptions = autoRemoveSubscriptions;
    }

    public List<SnsTopicDeliveryStatusAttribute> getDeliveryStatusAttributes() {
        return deliveryStatusAttributes;
    }

    public void setDeliveryStatusAttributes(List<SnsTopicDeliveryStatusAttribute> deliveryStatusAttributes) {
        this.deliveryStatusAttributes = deliveryStatusAttributes;
    }

    public Boolean getServerSideEncryption() {
        return serverSideEncryption;
    }

    public void setServerSideEncryption(Boolean serverSideEncryption) {
        this.serverSideEncryption = serverSideEncryption;
    }

    public String getKmsMasterKeyId() {
        return kmsMasterKeyId;
    }

    public void setKmsMasterKeyId(String kmsMasterKeyId) {
        this.kmsMasterKeyId = kmsMasterKeyId;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SnsTopic.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("policyName='" + policyName + "'")
                .add("autoRemoveSubscriptions=" + autoRemoveSubscriptions)
                .add("serverSideEncryption=" + serverSideEncryption)
                .add("kmsMasterKeyId='" + kmsMasterKeyId + "'")
                .add("subscriptions=" + subscriptions)
                .add("deliveryStatusAttributes=" + deliveryStatusAttributes)
                .toString();
    }
}
