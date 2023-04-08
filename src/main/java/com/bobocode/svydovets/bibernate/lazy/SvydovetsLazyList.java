package com.bobocode.svydovets.bibernate.lazy;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SvydovetsLazyList<T> implements List<T> {
    private final Supplier<List<T>> collectionSupplier;
    private List<T> internalList;

    public SvydovetsLazyList(Supplier<List<T>> collectionSupplier) {
        this.collectionSupplier = collectionSupplier;
    }

    private List<T> getInternalList() {
        if (internalList == null) {
            log.trace("Initializing lazy list");
            internalList = collectionSupplier.get();
        }
        return internalList;
    }

    @Override
    public int size() {
        return getInternalList().size();
    }

    @Override
    public boolean isEmpty() {
        return getInternalList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getInternalList().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return getInternalList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getInternalList().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] array) {
        return getInternalList().toArray(array);
    }

    @Override
    public boolean add(T t) {
        return getInternalList().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getInternalList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return getInternalList().contains(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return getInternalList().addAll(collection);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> collection) {
        return getInternalList().addAll(i, collection);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return getInternalList().removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return getInternalList().retainAll(collection);
    }

    @Override
    public void clear() {
        getInternalList().clear();
    }

    @Override
    public T get(int i) {
        return getInternalList().get(i);
    }

    @Override
    public T set(int i, T t) {
        return getInternalList().set(i, t);
    }

    @Override
    public void add(int i, T t) {
        getInternalList().add(i, t);
    }

    @Override
    public T remove(int i) {
        return getInternalList().remove(i);
    }

    @Override
    public int indexOf(Object o) {
        return getInternalList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getInternalList().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getInternalList().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return getInternalList().listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) {
        return getInternalList().subList(i, i1);
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        getInternalList().replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super T> c) {
        getInternalList().sort(c);
    }

    @Override
    public Spliterator<T> spliterator() {
        return getInternalList().spliterator();
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return getInternalList().toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        return getInternalList().removeIf(filter);
    }

    @Override
    public Stream<T> stream() {
        return getInternalList().stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return getInternalList().parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        getInternalList().forEach(action);
    }

    @Override
    public int hashCode() {
        return getInternalList().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getInternalList().equals(obj);
    }

    @Override
    public String toString() {
        return getInternalList().toString();
    }
}
