package ru.tggc.botapp.mapper;

public interface Mappable<E, D> {

    D toDto(E entity);
}
