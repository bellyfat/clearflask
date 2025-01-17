/*
 * This file is generated by jOOQ.
 */
package com.smotana.clearflask.store.mysql.model.tables.records;


import com.smotana.clearflask.store.mysql.model.tables.JooqIdea;

import java.time.Instant;

import javax.annotation.processing.Generated;

import org.jooq.Record2;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.16.10"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class JooqIdeaRecord extends UpdatableRecordImpl<JooqIdeaRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>idea.projectId</code>.
     */
    public void setProjectid(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>idea.projectId</code>.
     */
    public String getProjectid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>idea.postId</code>.
     */
    public void setPostid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>idea.postId</code>.
     */
    public String getPostid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>idea.authorUserId</code>.
     */
    public void setAuthoruserid(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>idea.authorUserId</code>.
     */
    public String getAuthoruserid() {
        return (String) get(2);
    }

    /**
     * Setter for <code>idea.authorName</code>.
     */
    public void setAuthorname(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>idea.authorName</code>.
     */
    public String getAuthorname() {
        return (String) get(3);
    }

    /**
     * Setter for <code>idea.authorIsMod</code>.
     */
    public void setAuthorismod(Boolean value) {
        set(4, value);
    }

    /**
     * Getter for <code>idea.authorIsMod</code>.
     */
    public Boolean getAuthorismod() {
        return (Boolean) get(4);
    }

    /**
     * Setter for <code>idea.created</code>.
     */
    public void setCreated(Instant value) {
        set(5, value);
    }

    /**
     * Getter for <code>idea.created</code>.
     */
    public Instant getCreated() {
        return (Instant) get(5);
    }

    /**
     * Setter for <code>idea.lastActivity</code>.
     */
    public void setLastactivity(Instant value) {
        set(6, value);
    }

    /**
     * Getter for <code>idea.lastActivity</code>.
     */
    public Instant getLastactivity() {
        return (Instant) get(6);
    }

    /**
     * Setter for <code>idea.title</code>.
     */
    public void setTitle(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>idea.title</code>.
     */
    public String getTitle() {
        return (String) get(7);
    }

    /**
     * Setter for <code>idea.description</code>.
     */
    public void setDescription(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>idea.description</code>.
     */
    public String getDescription() {
        return (String) get(8);
    }

    /**
     * Setter for <code>idea.response</code>.
     */
    public void setResponse(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>idea.response</code>.
     */
    public String getResponse() {
        return (String) get(9);
    }

    /**
     * Setter for <code>idea.responseAuthorUserId</code>.
     */
    public void setResponseauthoruserid(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>idea.responseAuthorUserId</code>.
     */
    public String getResponseauthoruserid() {
        return (String) get(10);
    }

    /**
     * Setter for <code>idea.responseAuthorName</code>.
     */
    public void setResponseauthorname(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>idea.responseAuthorName</code>.
     */
    public String getResponseauthorname() {
        return (String) get(11);
    }

    /**
     * Setter for <code>idea.categoryId</code>.
     */
    public void setCategoryid(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>idea.categoryId</code>.
     */
    public String getCategoryid() {
        return (String) get(12);
    }

    /**
     * Setter for <code>idea.statusId</code>.
     */
    public void setStatusid(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>idea.statusId</code>.
     */
    public String getStatusid() {
        return (String) get(13);
    }

    /**
     * Setter for <code>idea.commentCount</code>.
     */
    public void setCommentcount(Long value) {
        set(14, value);
    }

    /**
     * Getter for <code>idea.commentCount</code>.
     */
    public Long getCommentcount() {
        return (Long) get(14);
    }

    /**
     * Setter for <code>idea.childCommentCount</code>.
     */
    public void setChildcommentcount(Long value) {
        set(15, value);
    }

    /**
     * Getter for <code>idea.childCommentCount</code>.
     */
    public Long getChildcommentcount() {
        return (Long) get(15);
    }

    /**
     * Setter for <code>idea.funded</code>.
     */
    public void setFunded(Long value) {
        set(16, value);
    }

    /**
     * Getter for <code>idea.funded</code>.
     */
    public Long getFunded() {
        return (Long) get(16);
    }

    /**
     * Setter for <code>idea.fundGoal</code>.
     */
    public void setFundgoal(Long value) {
        set(17, value);
    }

    /**
     * Getter for <code>idea.fundGoal</code>.
     */
    public Long getFundgoal() {
        return (Long) get(17);
    }

    /**
     * Setter for <code>idea.fundersCount</code>.
     */
    public void setFunderscount(Long value) {
        set(18, value);
    }

    /**
     * Getter for <code>idea.fundersCount</code>.
     */
    public Long getFunderscount() {
        return (Long) get(18);
    }

    /**
     * Setter for <code>idea.voteValue</code>.
     */
    public void setVotevalue(Long value) {
        set(19, value);
    }

    /**
     * Getter for <code>idea.voteValue</code>.
     */
    public Long getVotevalue() {
        return (Long) get(19);
    }

    /**
     * Setter for <code>idea.votersCount</code>.
     */
    public void setVoterscount(Long value) {
        set(20, value);
    }

    /**
     * Getter for <code>idea.votersCount</code>.
     */
    public Long getVoterscount() {
        return (Long) get(20);
    }

    /**
     * Setter for <code>idea.expressionsValue</code>.
     */
    public void setExpressionsvalue(Double value) {
        set(21, value);
    }

    /**
     * Getter for <code>idea.expressionsValue</code>.
     */
    public Double getExpressionsvalue() {
        return (Double) get(21);
    }

    /**
     * Setter for <code>idea.trendScore</code>.
     */
    public void setTrendscore(Double value) {
        set(22, value);
    }

    /**
     * Getter for <code>idea.trendScore</code>.
     */
    public Double getTrendscore() {
        return (Double) get(22);
    }

    /**
     * Setter for <code>idea.mergedToPostId</code>.
     */
    public void setMergedtopostid(String value) {
        set(23, value);
    }

    /**
     * Getter for <code>idea.mergedToPostId</code>.
     */
    public String getMergedtopostid() {
        return (String) get(23);
    }

    /**
     * Setter for <code>idea.order</code>.
     */
    public void setOrder(Double value) {
        set(24, value);
    }

    /**
     * Getter for <code>idea.order</code>.
     */
    public Double getOrder() {
        return (Double) get(24);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached JooqIdeaRecord
     */
    public JooqIdeaRecord() {
        super(JooqIdea.IDEA);
    }

    /**
     * Create a detached, initialised JooqIdeaRecord
     */
    public JooqIdeaRecord(String projectid, String postid, String authoruserid, String authorname, Boolean authorismod, Instant created, Instant lastactivity, String title, String description, String response, String responseauthoruserid, String responseauthorname, String categoryid, String statusid, Long commentcount, Long childcommentcount, Long funded, Long fundgoal, Long funderscount, Long votevalue, Long voterscount, Double expressionsvalue, Double trendscore, String mergedtopostid, Double order) {
        super(JooqIdea.IDEA);

        setProjectid(projectid);
        setPostid(postid);
        setAuthoruserid(authoruserid);
        setAuthorname(authorname);
        setAuthorismod(authorismod);
        setCreated(created);
        setLastactivity(lastactivity);
        setTitle(title);
        setDescription(description);
        setResponse(response);
        setResponseauthoruserid(responseauthoruserid);
        setResponseauthorname(responseauthorname);
        setCategoryid(categoryid);
        setStatusid(statusid);
        setCommentcount(commentcount);
        setChildcommentcount(childcommentcount);
        setFunded(funded);
        setFundgoal(fundgoal);
        setFunderscount(funderscount);
        setVotevalue(votevalue);
        setVoterscount(voterscount);
        setExpressionsvalue(expressionsvalue);
        setTrendscore(trendscore);
        setMergedtopostid(mergedtopostid);
        setOrder(order);
    }
}
