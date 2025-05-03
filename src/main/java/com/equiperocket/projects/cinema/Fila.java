package com.equiperocket.projects.cinema;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Fila<T> implements Iterable<T> {

    private class Element {
        private T value;
        private Element next;

        public Element(T value) {
            this.value = value;
            next = null;
        }
    }

    private Element first, last;
    private int tamanho;

    private static final String ERRO_FILA_VAZIA = "A fila está vazia";
    private static final String ERRO_ELEMENTO_NULO = "Não é permitido inserir elementos nulos";

    public Fila() {
        first = null;
        last = null;
        tamanho = 0;
    }

    public void enfileirar(T elemento) {
        // Validação de elemento nulo antes de qualquer operação
        if (elemento == null) {
            throw new NullPointerException(ERRO_ELEMENTO_NULO);
        }
        
        Element e = new Element(elemento);
        if (estaVazia()) {
            first = e;
            last = e;
        } else {
            last.next = e;
            last = e;
        }
        tamanho++;
    }

    public T desenfileirar() {
        validarFilaVazia();

        Element e = first;
        first = first.next;
        tamanho--;
        
        // Se a fila ficou vazia, atualiza o último também
        if (first == null) {
            last = null;
        }
        
        return e.value;
    }

    public T primeiro() {
        validarFilaVazia();
        return first.value;
    }

    public void limpar() {
        first = null;
        last = null;
        tamanho = 0;
    }

    // Métodos de verificação de estado
    public boolean estaVazia() {
        return tamanho == 0;
    }

    // Mantido para compatibilidade, mas sempre retorna false pois a fila agora é dinâmica
    public boolean estaCheia() {
        return false;
    }

    public int tamanho() {
        return tamanho;
    }

    // Mantido para compatibilidade, mas retorna -1 pois não há mais capacidade fixa
    public int capacidade() {
        return -1;
    }

    // Métodos auxiliares privados
    private void validarFilaVazia() {
        if (estaVazia()) {
            throw new IllegalStateException(ERRO_FILA_VAZIA);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Element currentElement = first;

            @Override
            public boolean hasNext() {
                return currentElement != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T item = currentElement.value;
                currentElement = currentElement.next;
                return item;
            }
        };
    }
}
