/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.optimize;

import com.google.common.base.Optional;
import lombok.SneakyThrows;
import org.apache.shardingsphere.core.parse.antlr.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.core.parse.old.parser.context.condition.Column;
import org.apache.shardingsphere.core.parse.old.parser.context.condition.GeneratedKeyCondition;
import org.apache.shardingsphere.core.parse.old.parser.context.insertvalue.InsertValue;
import org.apache.shardingsphere.core.parse.old.parser.context.table.Tables;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public final class GeneratedKeyTest {
    
    @Mock
    private ShardingRule shardingRule;
    
    @Mock
    private InsertStatement insertStatement;
    
    @Before
    public void setUp() {
        Tables tables = mock(Tables.class);
        when(insertStatement.getTables()).thenReturn(tables);
        when(tables.getSingleTableName()).thenReturn("tbl");
        when(insertStatement.getColumnNames()).thenReturn(Collections.singletonList("id"));
        when(insertStatement.getValues()).thenReturn(Collections.singletonList(mock(InsertValue.class)));
    }
    
    @Test
    public void assertGetGenerateKeyWhenCreateWithoutGenerateKeyColumnConfiguration() {
        when(shardingRule.findGenerateKeyColumnName("tbl")).thenReturn(Optional.<String>absent());
        assertFalse(GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement).isPresent());
    }
    
    @Test
    public void assertGetGenerateKeyWhenCreateWithGenerateKeyColumnConfiguration() {
        when(shardingRule.findGenerateKeyColumnName("tbl")).thenReturn(Optional.of("id1"));
        Optional<GeneratedKey> actual = GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement);
        assertTrue(actual.isPresent());
        assertThat(actual.get().getGeneratedKeys().size(), is(1));
    }
    
    @Test
    public void assertGetGenerateKeyWhenFind() {
        mockGetGenerateKeyWhenFind();
        assertTrue(GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement).isPresent());
    }
    
    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void mockGetGenerateKeyWhenFind() {
        GeneratedKeyCondition generatedKeyCondition = mock(GeneratedKeyCondition.class);
        when(generatedKeyCondition.getIndex()).thenReturn(-1);
        when(generatedKeyCondition.getValue()).thenReturn((Comparable) 100);
        when(generatedKeyCondition.getColumn()).thenReturn(new Column("id", "tbl"));
        when(insertStatement.getGeneratedKeyConditions()).thenReturn(Arrays.asList(generatedKeyCondition, mock(GeneratedKeyCondition.class)));
        when(shardingRule.findGenerateKeyColumnName("tbl")).thenReturn(Optional.of("id"));
        Optional<GeneratedKey> actual = GeneratedKey.getGenerateKey(shardingRule, Collections.<Object>singletonList(1), insertStatement);
        assertTrue(actual.isPresent());
        assertThat(actual.get().getGeneratedKeys().size(), is(2));
        assertThat(actual.get().getGeneratedKeys().get(0), is((Comparable) 100));
        assertThat(actual.get().getGeneratedKeys().get(1), is((Comparable) 1));
    }
}
