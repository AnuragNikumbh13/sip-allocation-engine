package sip_allocation_engine.exception;

public class FundNotFoundException extends RuntimeException {
    public FundNotFoundException(String message){
        super(message);
    }
}
