package com.arexnt.sms.data;

public class Conversation {
    private long mID;
    private String name;
    private String mDate;
    private int mMessageCount;
    private long mRecipient_id;
    private String mSnippet;
    private boolean mHasUnreadMessages;
    private boolean mHasAttachment;
    private boolean mHasError;
    private boolean mIsChecked;
    private String mAddress;
    private int mType;
    private boolean isData;
    private String company;

    public Conversation(){

    }

    public Conversation(long ID, String date, int messageCount,
                        long recipient_id, String snippet, boolean hasUnreadMessages,
                        boolean hasAttachment, boolean hasError, boolean isChecked,
                        String address) {

        mID = ID;
        mDate = date;
        mMessageCount = messageCount;
        mRecipient_id = recipient_id;
        mSnippet = snippet;
        mHasUnreadMessages = hasUnreadMessages;
        mHasAttachment = hasAttachment;
        mHasError = hasError;
        mIsChecked = isChecked;
        mAddress = address;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "mID=" + mID +
                ", mDate=" + mDate +
                ", mMessageCount=" + mMessageCount +
                ", mRecipient_id=" + mRecipient_id +
                ", mSnippet='" + mSnippet + '\'' +
                ", mHasUnreadMessages=" + mHasUnreadMessages +
                ", mHasAttachment=" + mHasAttachment +
                ", mHasError=" + mHasError +
                ", mIsChecked=" + mIsChecked +
                ", mAddress='" + mAddress + '\'' +
                '}';
    }


    public long getID() {
        return mID;
    }

    public void setID(long ID) {
        mID = ID;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public int getMessageCount() {
        return mMessageCount;
    }

    public void setMessageCount(int messageCount) {
        mMessageCount = messageCount;
    }

    public long getRecipient_id() {
        return mRecipient_id;
    }

    public void setRecipient_id(long recipient_id) {
        mRecipient_id = recipient_id;
    }

    public String getSnippet() {
        return mSnippet;
    }

    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }

    public boolean isHasUnreadMessages() {
        return mHasUnreadMessages;
    }

    public void setHasUnreadMessages(boolean hasUnreadMessages) {
        mHasUnreadMessages = hasUnreadMessages;
    }

    public boolean isHasAttachment() {
        return mHasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        mHasAttachment = hasAttachment;
    }

    public boolean isHasError() {
        return mHasError;
    }

    public void setHasError(boolean hasError) {
        mHasError = hasError;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isData() {
        return isData;
    }

    public void setData(boolean data) {
        isData = data;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}


