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

package org.apache.shardingsphere.core.parse.antlr.extractor.impl.dml.select.item;

import com.google.common.base.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.shardingsphere.core.parse.antlr.extractor.api.OptionalSQLSegmentExtractor;
import org.apache.shardingsphere.core.parse.antlr.extractor.util.ExtractorUtils;
import org.apache.shardingsphere.core.parse.antlr.extractor.util.RuleName;
import org.apache.shardingsphere.core.parse.antlr.sql.segment.dml.item.ColumnSelectItemSegment;

/**
 * Column select item segment extractor.
 *
 * @author zhangliang
 */
public final class ColumnSelectItemSegmentExtractor implements OptionalSQLSegmentExtractor {
    
    @Override
    public Optional<ColumnSelectItemSegment> extract(final ParserRuleContext expressionNode) {
        if (!RuleName.COLUMN_NAME.getName().equals(expressionNode.getChild(0).getClass().getSimpleName())) {
            return Optional.absent();
        }
        ParserRuleContext columnNode = (ParserRuleContext) expressionNode.getChild(0);
        ColumnSelectItemSegment result = new ColumnSelectItemSegment(columnNode.getText(), columnNode.getStart().getStartIndex(), columnNode.getStop().getStopIndex());
        Optional<ParserRuleContext> aliasNode = ExtractorUtils.findFirstChildNode(expressionNode, RuleName.ALIAS);
        if (aliasNode.isPresent()) {
            result.setAlias(aliasNode.get().getText());
        }
        return Optional.of(result);
    }
}
