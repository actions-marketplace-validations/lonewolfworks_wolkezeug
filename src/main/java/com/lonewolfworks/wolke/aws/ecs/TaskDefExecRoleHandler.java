package com.lonewolfworks.wolke.aws.ecs;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import com.amazonaws.services.ecs.model.ContainerDefinition;
import com.amazonaws.services.ecs.model.Secret;

public class TaskDefExecRoleHandler {
	
	public String generateTaskDefIam(EcsPushDefinition definition) {
	
		//check for secrets
		Set<String> secArns = new HashSet();
		for(ContainerDefinition def : definition.getContainerDefinitions()) {
    		for(Secret sec : def.getSecrets()) {
    			if(sec.getValueFrom().contains("arn:")){
    				secArns.add(sec.getValueFrom());
    			}
    		}
    	}
		if(secArns.size()==0) {
			//no need
			return null;
		}
		Set<String> containers = new HashSet();
		for(ContainerDefinition def : definition.getContainerDefinitions()) {
			///###.dkr.ecr.us-east-1.amazonaws.com/image:tag
			if(def.getImage().contains(".dkr.ecr.")) {
				
				String root = def.getImage().split("/")[0];
				String imageAndTag = def.getImage().split("/")[1];
				String image = imageAndTag.split(":")[0];
				
				String[] split = root.split("\\.");
				//arn:aws:ecr:<region>:####:repository/<images>
				containers.add("arn:aws:ecr:"+split[3]+":"+split[0]+":repository/"+image);
			}
			
		}
		
		return renderPolicy(secArns, containers);
	
		
	}
	
	private String renderPolicy(Set<String> secrets, Set<String> containers) {
		StringBuilder buf = new StringBuilder();
		buf.append("{\n" + 
				"    \"Version\": \"2012-10-17\",\n" + 
				"    \"Statement\": [");
		
		StringJoiner joiner = new StringJoiner(",");
		for(String s : secrets) {
			joiner.add("        {\n" + 
					"            \"Action\": [\n" + 
					"                \"secretsmanager:GetSecretValue\",\n" + 
					"                \"secretsmanager:DescribeSecret\"\n" + 
					"            ],\n" + 
					"            \"Resource\": \""+s+"\",\n" + 
					"            \"Effect\": \"Allow\"\n" + 
					"        }");
		}
		for(String c : containers) {
			joiner.add("        {\n" + 
					"            \"Action\": [\n" + 
					"                \"ecr:BatchCheckLayerAvailability\",\n" + 
					"                \"ecr:GetDownloadUrlForLayer\",\n" + 
					"                \"ecr:BatchGetImage\"\n" + 
					"            ],\n" + 
					"            \"Resource\": \""+c+"\",\n" + 
					"            \"Effect\": \"Allow\"\n" + 
					"        }");
		}
		
		
		buf.append(joiner.toString());

		buf.append("    ]\n" + 
				"}");
		
		return buf.toString();
	}

}
