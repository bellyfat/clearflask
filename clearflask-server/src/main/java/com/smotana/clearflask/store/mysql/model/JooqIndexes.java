/*
 * This file is generated by jOOQ.
 */
package com.smotana.clearflask.store.mysql.model;


import com.smotana.clearflask.store.mysql.model.tables.JooqComment;
import com.smotana.clearflask.store.mysql.model.tables.JooqCommentParentId;
import com.smotana.clearflask.store.mysql.model.tables.JooqIdea;
import com.smotana.clearflask.store.mysql.model.tables.JooqIdeaFunders;
import com.smotana.clearflask.store.mysql.model.tables.JooqUser;

import javax.annotation.processing.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables in the default schema.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.16.10"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JooqIndexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index COMMENT_COMMENT_AUTHORUSERID_IDX = Internal.createIndex(DSL.name("comment_authorUserId_idx"), JooqComment.COMMENT, new OrderField[] { JooqComment.COMMENT.AUTHORUSERID }, false);
    public static final Index COMMENT_PARENT_ID_COMMENT_PARENT_ID_PROJECTID_POSTID_IDX = Internal.createIndex(DSL.name("comment_parent_id_projectId_postId_idx"), JooqCommentParentId.COMMENT_PARENT_ID, new OrderField[] { JooqCommentParentId.COMMENT_PARENT_ID.PROJECTID, JooqCommentParentId.COMMENT_PARENT_ID.POSTID }, false);
    public static final Index COMMENT_COMMENT_PROJECTID_IDX = Internal.createIndex(DSL.name("comment_projectId_idx"), JooqComment.COMMENT, new OrderField[] { JooqComment.COMMENT.PROJECTID }, false);
    public static final Index COMMENT_COMMENT_PROJECTID_POSTID_IDX = Internal.createIndex(DSL.name("comment_projectId_postId_idx"), JooqComment.COMMENT, new OrderField[] { JooqComment.COMMENT.PROJECTID, JooqComment.COMMENT.POSTID }, false);
    public static final Index IDEA_IDEA_AUTHORUSERID_IDX = Internal.createIndex(DSL.name("idea_authorUserId_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.AUTHORUSERID }, false);
    public static final Index IDEA_IDEA_CATEGORYID_IDX = Internal.createIndex(DSL.name("idea_categoryId_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.CATEGORYID }, false);
    public static final Index IDEA_IDEA_CREATED_IDX = Internal.createIndex(DSL.name("idea_created_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.CREATED }, false);
    public static final Index IDEA_IDEA_LASTACTIVITY_IDX = Internal.createIndex(DSL.name("idea_lastActivity_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.LASTACTIVITY }, false);
    public static final Index IDEA_IDEA_MERGEDTOPOSTID_IDX = Internal.createIndex(DSL.name("idea_mergedToPostId_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.MERGEDTOPOSTID }, false);
    public static final Index IDEA_IDEA_PROJECTID_IDX = Internal.createIndex(DSL.name("idea_projectId_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.PROJECTID }, false);
    public static final Index IDEA_IDEA_STATUSID_IDX = Internal.createIndex(DSL.name("idea_statusId_idx"), JooqIdea.IDEA, new OrderField[] { JooqIdea.IDEA.STATUSID }, false);
    public static final Index IDEA_FUNDERS_PROJECTID = Internal.createIndex(DSL.name("projectId"), JooqIdeaFunders.IDEA_FUNDERS, new OrderField[] { JooqIdeaFunders.IDEA_FUNDERS.PROJECTID, JooqIdeaFunders.IDEA_FUNDERS.FUNDERUSERID }, false);
    public static final Index USER_USER_ISMOD_IDX = Internal.createIndex(DSL.name("user_isMod_idx"), JooqUser.USER, new OrderField[] { JooqUser.USER.ISMOD }, false);
    public static final Index USER_USER_PROJECTID_IDX = Internal.createIndex(DSL.name("user_projectId_idx"), JooqUser.USER, new OrderField[] { JooqUser.USER.PROJECTID }, false);
}
