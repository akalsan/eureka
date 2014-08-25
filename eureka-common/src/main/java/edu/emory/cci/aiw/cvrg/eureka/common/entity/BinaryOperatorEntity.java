package edu.emory.cci.aiw.cvrg.eureka.common.entity;

/*
 * #%L
 * Eureka Common
 * %%
 * Copyright (C) 2012 - 2014 Emory University
 * %%
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
 * #L%
 */

import edu.emory.cci.aiw.cvrg.eureka.common.comm.BinaryOperator;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.Node;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Andrew Post
 */
@Entity
@Table(name = "binary_operators")
public class BinaryOperatorEntity extends NodeEntity {
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BinaryOperator.Op op;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private NodeEntity leftNode;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false)
	private NodeEntity rightNode;

	public BinaryOperator.Op getOp() {
		return op;
	}

	public void setOp(BinaryOperator.Op op) {
		this.op = op;
	}

	public NodeEntity getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(NodeEntity leftNode) {
		this.leftNode = leftNode;
	}

	public NodeEntity getRightNode() {
		return rightNode;
	}

	public void setRightNode(NodeEntity rightNode) {
		this.rightNode = rightNode;
	}

	@Override
	public Node toNode() {
		BinaryOperator bo = new BinaryOperator();
		bo.setOp(this.op);
		bo.setLeftNode(this.leftNode.toNode());
		bo.setRightNode(this.rightNode.toNode());
		return bo;
	}
	
	
}