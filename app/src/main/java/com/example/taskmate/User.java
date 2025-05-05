package com.example.taskmate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class User {
    public String email;
    public String password;
    public String date_of_registration;
    public List<String> id_of_tasks = new List<String>() {
        @Override
        public int size() {
            return id_of_tasks.size();
        }

        @Override
        public boolean isEmpty() {
            return id_of_tasks.isEmpty();
        }

        @Override
        public boolean contains(@Nullable Object o) {
            return id_of_tasks.contains(o);
        }

        @NonNull
        @Override
        public Iterator<String> iterator() {
            return id_of_tasks.iterator();
        }

        @NonNull
        @Override
        public Object[] toArray() {
            return id_of_tasks.toArray();
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            return id_of_tasks.toArray(a);
        }

        // --- Добавление/удаление элементов ---
        @Override
        public boolean add(String s) {
            return id_of_tasks.add(s);
        }

        @Override
        public boolean remove(@Nullable Object o) {
            return id_of_tasks.remove(o);
        }

        // --- Пакетные операции ---
        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            return id_of_tasks.containsAll(c);
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends String> c) {
            return id_of_tasks.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends String> c) {
            return id_of_tasks.addAll(index, c);
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            return id_of_tasks.removeAll(c);
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            return id_of_tasks.retainAll(c);
        }

        @Override
        public void clear() {
            id_of_tasks.clear();
        }

        // --- Получение и изменение по индексу ---
        @Override
        public String get(int index) {
            return id_of_tasks.get(index);
        }

        @Override
        public String set(int index, String element) {
            return id_of_tasks.set(index, element);
        }

        @Override
        public void add(int index, String element) {
            id_of_tasks.add(index, element);
        }

        @Override
        public String remove(int index) {
            return id_of_tasks.remove(index);
        }

        // --- Поиск индекса ---
        @Override
        public int indexOf(@Nullable Object o) {
            return id_of_tasks.indexOf(o);
        }

        @Override
        public int lastIndexOf(@Nullable Object o) {
            return id_of_tasks.lastIndexOf(o);
        }

        // --- ListIterator ---
        @NonNull
        @Override
        public ListIterator<String> listIterator() {
            return id_of_tasks.listIterator();
        }

        @NonNull
        @Override
        public ListIterator<String> listIterator(int index) {
            return id_of_tasks.listIterator(index);
        }

        // --- Подсписок ---
        @NonNull
        @Override
        public List<String> subList(int fromIndex, int toIndex) {
            return id_of_tasks.subList(fromIndex, toIndex);
        }
    };
}
