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
package com.lonewolfworks.wolke.aws.ecs.loadbalancing;

import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerNotFoundException;
import com.lonewolfworks.wolke.aws.ecs.EcsPushDefinition;
import com.lonewolfworks.wolke.logging.HermanLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElbOrAlbDecider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElbOrAlbDecider.class);

    private AmazonElasticLoadBalancing elbClient;
    private HermanLogger buildLogger;

    public ElbOrAlbDecider(AmazonElasticLoadBalancing elbClient, HermanLogger buildLogger) {
        this.elbClient = elbClient;
        this.buildLogger = buildLogger;
    }

    public boolean shouldUseAlb(String serviceName, EcsPushDefinition definition) {
        LoadBalancerDescription balancer = null;
        try {
            balancer = elbClient
                .describeLoadBalancers(new DescribeLoadBalancersRequest().withLoadBalancerNames(serviceName))
                .getLoadBalancerDescriptions().get(0);
            buildLogger.addLogEntry("ELB found: " + serviceName);
        } catch (LoadBalancerNotFoundException e) {
            LOGGER.debug("Error getting ELB " + serviceName, e);
        }

        if (balancer != null && "internet-facing".equalsIgnoreCase(balancer.getScheme())) {
            buildLogger.addLogEntry("Retaining ELB since app is existing and external - needs manual DNS work");
            return false;
        } else if (definition.getService().getHealthCheck().getTarget().toUpperCase().contains("TCP")) {
            buildLogger.addLogEntry(
                "Using ELB as healthcheck is set to TCP; not supported by ALB and can't be used for blue-green");
            return false;
        } else if (definition.getService().getProtocol() != null && definition.getService().getProtocol().toUpperCase()
            .contains("TCP")) {
            buildLogger.addLogEntry("Using ELB as endpoint is set to TCP; not supported by ALB");
            return false;
        } else if ("true".equalsIgnoreCase(definition.getUseElb())) {
            buildLogger.addLogEntry("Using ELB as override flag is set");
            return false;
        }

        return true;
    }
}
