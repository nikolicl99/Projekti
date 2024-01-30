package oop.klub.luka_nikolic.Exceptions;

public class MessageContainsCharacters extends RuntimeException {
    public MessageContainsCharacters(String message){
        super(message);
    }
}
