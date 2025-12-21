package com.shout.exception;

public class RateLimitExceededException extends RuntimeException {
    private final long remainingRequests;
    private final long resetTime;
    
    public RateLimitExceededException(String message, long remainingRequests, long resetTime) {
        super(message);
        this.remainingRequests = remainingRequests;
        this.resetTime = resetTime;
    }
    
    public long getRemainingRequests() {
        return remainingRequests;
    }
    
    public long getResetTime() {
        return resetTime;
    }
}
