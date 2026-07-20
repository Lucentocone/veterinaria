package com.unam.exception;

public class CancelacionFueraDeTerminoException extends RuntimeException {
    public CancelacionFueraDeTerminoException(String mensaje) { super(mensaje); }
}