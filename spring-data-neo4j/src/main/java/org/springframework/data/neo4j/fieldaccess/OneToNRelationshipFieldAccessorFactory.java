/**
 * Copyright 2011 the original author or authors.
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

package org.springframework.data.neo4j.fieldaccess;

import static org.springframework.data.neo4j.support.DoReturn.doReturn;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.springframework.data.neo4j.core.NodeBacked;
import org.springframework.data.neo4j.mapping.Neo4JPersistentProperty;
import org.springframework.data.neo4j.mapping.RelationshipInfo;
import org.springframework.data.neo4j.support.GraphDatabaseContext;

public class OneToNRelationshipFieldAccessorFactory<T extends NodeBacked, TARGET extends NodeBacked> extends NodeRelationshipFieldAccessorFactory<T, TARGET> {
	
	public OneToNRelationshipFieldAccessorFactory(GraphDatabaseContext graphDatabaseContext) {
		super(graphDatabaseContext);
	}

    @Override
    public boolean accept(final Neo4JPersistentProperty property) {
        if (!property.isRelationship()) return false;
        final RelationshipInfo info = property.getRelationshipInfo();
        return info.isMultiple() && info.targetsNodes() && !info.isReadonly();
    }

    @Override
	public FieldAccessor<T> forField(final Neo4JPersistentProperty property) {
        final RelationshipInfo relationshipInfo = property.getRelationshipInfo();
        final Class<TARGET> targetType = (Class<TARGET>) relationshipInfo.getTargetType().getType();
        return new OneToNRelationshipFieldAccessor<T, TARGET>(relationshipInfo.getRelationshipType(), relationshipInfo.getDirection(), targetType, graphDatabaseContext,property);
	}

	public static class OneToNRelationshipFieldAccessor<T extends NodeBacked, TARGET extends NodeBacked> extends NodeToNodesRelationshipFieldAccessor<T, TARGET> {

	    public OneToNRelationshipFieldAccessor(final RelationshipType type, final Direction direction, final Class<TARGET> elementClass, final GraphDatabaseContext graphDatabaseContext, Neo4JPersistentProperty property) {
	        super(elementClass, graphDatabaseContext, direction, type,property);
	    }

	    @Override
		public Object setValue(final T entity, final Object newVal) {
	        final Node node = checkUnderlyingNode(entity);
	        if (newVal == null) {
	            removeMissingRelationships(node, Collections.<Node>emptySet());
	            return null;
	        }
	        final Set<Node> targetNodes = checkTargetIsSetOfNodebacked(newVal);
	        removeMissingRelationships(node, targetNodes);
	        createAddedRelationships(node, targetNodes);
	        return createManagedSet(entity, (Set<TARGET>) newVal);
	    }

	    @Override
	    public Object getValue(final T entity) {
	        checkUnderlyingNode(entity);
	        final Set<TARGET> result = createEntitySetFromRelationshipEndNodes(entity);
	        return doReturn(createManagedSet(entity, result));
	    }

	    @Override
		public Object getDefaultImplementation() {
	        return new HashSet<TARGET>();
		}
	}
}
