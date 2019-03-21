/**
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.neo4j.support;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.springframework.data.neo4j.support.HasRelationshipMatcher.hasRelationship;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.support.node.Neo4jHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {"classpath:org/springframework/data/neo4j/support/Neo4jGraphPersistenceTest-context.xml"} )
public class HasRelationshipMatcherTest
{
    @Autowired
    private GraphDatabaseContext db;

    @Before
    public void cleanDb() {
        Neo4jHelper.cleanDb(db);
    }

    @Test
    @Transactional
	public void testHasRelationshipMatcher() {
    	final String foo = "foo";
    	Node n1 = db.getGraphDatabaseService().createNode();
    	Node n2 = db.getGraphDatabaseService().createNode();
    	n1.createRelationshipTo(n2, DynamicRelationshipType.withName(foo));
    	
    	assertThat(n1, hasRelationship("foo", n2));
    	assertThat(n1, hasRelationship("foo", null));
    	assertThat(n1, not(hasRelationship("bar", n2)));
    	assertThat(n1, not(hasRelationship("bar", null)));
	}
}
