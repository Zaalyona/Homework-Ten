import java.util.*;
import java.util.function.Consumer;

public class CustomLinkedList<B> extends AbstractSequentialList<B>
        implements List<B>, Deque<B>, Cloneable, java.io.Serializable {
    //размер связанного списка
    transient int size = 0;

    //Курсор на первую запись
    transient Node<B> first;

    //Курсор на последнюю запись
    transient Node<B> last;

    //Тут два конструктора
    //Первый для пустого листа
    public CustomLinkedList() {
    }

    //Второй создает список, содержащий элементы указанной коллекции, в том порядке, в котором они возвращаются
    //итератором коллекции
    //Может генерировать NullPointerException в случае, если коллекция в параметре пустая
    public CustomLinkedList(Collection<? extends B> c) {
        this();
        addAll(c);
    }

    //Создание и связывание первого элемента списка
    private void linkFirst(B e) {
        final Node<B> f = first;
        final Node<B> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }

    //Создание и связывание последнего элемента списка
    void linkLast(B e) {
        final Node<B> l = last;
        final Node<B> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    //Вставка нового элемента списка перед текущий (не должен быть пустым)
    void linkBefore(B e, Node<B> succ) {
        final Node<B> pred = succ.prev;
        final Node<B> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }

    //Стираем ссылку на первый элемент списка, записываем в первый элемент списка значение null
    private B unlinkFirst(Node<B> f) {
        final B element = f.item;
        final Node<B> next = f.next;
        f.item = null;
        f.next = null;
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }

    //Стираем ссылку на последний элемент списка, записываем в последний элемент списка значение null
    private B unlinkLast(Node<B> l) {
        final B element = l.item;
        final Node<B> prev = l.prev;
        l.item = null;
        l.prev = null;
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    //Стираем ссылку на определённый элемент списка, записываем в этот элемент списка значение null
    B unlink(Node<B> x) {
        final B element = x.item;
        final Node<B> next = x.next;
        final Node<B> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    //Получаем первый элемент списка
    //Может генерировать NullPointerException в случае, если первый элемент пустой
    public B getFirst() {
        final Node<B> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    //Получаем последний элемент списка
    //Может генерировать NullPointerException в случае, если последний элемент пустой
    public B getLast() {
        final Node<B> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    //Удаляем первый элемент списка и возвращаем его в конце использования метода
    //Может генерировать NoSuchElementException в случае, если первый элемент пустой
    public B removeFirst() {
        final Node<B> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    //Удаляем последний элемент списка и возвращаем его в конце использования метода
    //Может генерировать NoSuchElementException в случае, если последний элемент пустой
    public B removeLast() {
        final Node<B> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    //Добавляем элемент в начало списка
    public void addFirst(B e) {
        linkFirst(e);
    }

    //Добавляем элемент в конец списка
    public void addLast(B e) {
        linkLast(e);
    }

    //Проверяет, содержит ли список указанный в параметре элемент
    //Возвращает true тогда и только тогда, когда список содержит хотя бы один элемент e
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    //Возвращает размер списка
    public int size() {
        return size;
    }

    //Добавляет указанный элемент в конец списка
    //Работает аналогично методу addLast
    public boolean add(B e) {
        linkLast(e);
        return true;
    }

    //Удаляет первое вхождение указанного объекта из списка, если он присутствует
    //Если этот список не содержит объект, он остается неизменным
    //Возвращает true, если список содержит указанный объект (если список изменился в результате вызова метода)
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<B> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<B> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    //Добавляет все элементы указанной коллекции в конец списка в том порядке, в котором они возвращаются
    //итератором указанной коллекции. Поведение этой метода непредсказуемо, если указанная коллекция
    //изменяется во время выполнения операции
    //Возвращает true, если список изменился в результате работы метода
    //Может генерировать NullPointerException в случае, если передаваемая коллекция пустая
    public boolean addAll(Collection<? extends B> c) {
        return addAll(size, c);
    }

    //Вставляет все элементы указанной коллекции в список, начиная с указанного индекса
    //Сдвигает элемент, находящийся в данный момент в этой позиции (если есть), и любые последующие элементы
    //вправо (увеличивает их индексы). Новые элементы появятся в списке в том порядке, в котором они
    //возвращаются указанным итератором коллекции
    //Может генерировать исключения:
    //IndexOutOfBoundsException – если индекс выходит за пределы допустимого диапазона
    //NullPointerException — если указанная коллекция имеет значение null
    public boolean addAll(int index, Collection<? extends B> c) {
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;

        Node<B> pred, succ;
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }

        for (Object o : a) {
            B e = (B) o;
            Node<B> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }

    //Удаляет все элементы из списка. Список будет пуст после выполнения метода
    public void clear() {
        for (Node<B> x = first; x != null; ) {
            Node<B> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }

    //Метод получения элемента списка с определённым индексом
    //Может генерировать IndexOutOfBoundsException, если индекс оказался за пределами списка
    public B get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    //Заменяет элемент в указанной позиции списка указанным элементом
    //Может генерировать IndexOutOfBoundsException в случае, если передаваемый индекс находится за пределами списка
    public B set(int index, B element) {
        checkElementIndex(index);
        Node<B> x = node(index);
        B oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    //Вставляет указанный элемент в указанную позицию списка. Сдвигает элемент, находящийся в данный момент
    //в этой позиции (если есть) и любые последующие элементы вправо
    //Может генерировать IndexOutOfBoundsException в случае, если передаваемый индекс находится за пределами списка
    public void add(int index, B element) {
        checkPositionIndex(index);

        if (index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    //Удаляет элемент в указанной позиции списка. Сдвигает любые последующие элементы влево
    //Возвращает элемент, который был удален из списка
    //Может генерировать IndexOutOfBoundsException в случае, если передаваемый индекс находится за пределами списка
    public B remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    //Проверяет, является ли аргумент индексом существующего элемента
    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    //Проверяет, является ли аргумент индексом допустимой позиции для итератора или операции добавления
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    //Формирует корректное сообщение в случае выхода за пределы списка
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    //Метод проверки индекса списка
    private void checkElementIndex(int index) {
        if (!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    //Метод проверки позиции индекса итератора или операции добавления
    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    //Возвращает (непустой) узел с указанным индексом элемента
    Node<B> node(int index) {
        if (index < (size >> 1)) {
            Node<B> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<B> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    //Возвращает индекс первого вхождения указанного элемента в списке или -1, если список не содержит элемента
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<B> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<B> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    //Возвращает индекс последнего вхождения указанного элемента в списке или -1, если список не содержит элемента
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<B> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (Node<B> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }

    //Возвращает заголовок (первый элемент) списка
    public B peek() {
        final Node<B> f = first;
        return (f == null) ? null : f.item;
    }

    //Также возвращает заголовок (первый элемент) списка
    //Может генерировать NoSuchElementException в случае, если список пустой
    public B element() {
        return getFirst();
    }

    //Удаляет и возвращает заголовок (первый элемент) списка
    public B poll() {
        final Node<B> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    //Удаляет и возвращает заголовок (первый элемент) списка
    //Может генерировать NoSuchElementException в случае, если список пустой
    public B remove() {
        return removeFirst();
    }

    //Добавляет указанный элемент в конец списка
    public boolean offer(B e) {
        return add(e);
    }

    //Вставляет указанный элемент в начало списка
    public boolean offerFirst(B e) {
        addFirst(e);
        return true;
    }

    //Вставляет указанный элемент в конец списка
    public boolean offerLast(B e) {
        addLast(e);
        return true;
    }

    //Извлекает, но не удаляет, первый элемент списка или возвращает null, если список пуст
    public B peekFirst() {
        final Node<B> f = first;
        return (f == null) ? null : f.item;
    }

    //Извлекает, но не удаляет, последний элемент списка или возвращает null, если список пуст
    public B peekLast() {
        final Node<B> l = last;
        return (l == null) ? null : l.item;
    }

    //Извлекает и удаляет первый элемент списка или возвращает null, если список пуст
    public B pollFirst() {
        final Node<B> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    //Извлекает и удаляет последний элемент списка или возвращает null, если список пуст
    public B pollLast() {
        final Node<B> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    //Вставляет элемент в начале этого списка. Этот метод эквивалентен addFirst
    public void push(B e) {
        addFirst(e);
    }

    //Удаляет и возвращает первый элемент списка. Этот метод эквивалентен removeFirst()
    //Может генерировать NoSuchElementException в случае, если список пуст
    public B pop() {
        return removeFirst();
    }

    //Удаляет первое вхождение указанного элемента в списке (при проходе списка от начала до конца)
    //Возвращает true, если список содержит указанный элемент. Если список не содержит элемента, он не изменяется
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    //Удаляет последнее вхождение указанного элемента в списке (при проходе списка от начала до конца)
    //Возвращает true, если список содержит указанный элемент. Если список не содержит элемента, он не изменяется
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<B> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<B> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    //Возвращает итератор элементов в списке (в правильной последовательности), начиная с указанной позиции в списке
    //Итератор списка работает безотказно: если список структурно изменяется в любое время после создания
    //итератора любым способом, кроме как с помощью собственных методов удаления или добавления итератора списка,
    //итератор списка выдает исключение ConcurrentModificationException
    //Может генерировать IndexOutOfBoundsException в случае, если индекс вышел за пределы списка
    public ListIterator<B> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    //Внутренний класс итератора, где можно перейти к следующему элементу списка или предыдущему, добавить, удалить
    //или установить элемент итератора
    private class ListItr implements ListIterator<B> {
        private CustomLinkedList.Node<B> lastReturned;
        private CustomLinkedList.Node<B> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public B next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public B previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            CustomLinkedList.Node<B> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(B e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(B e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super B> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    //Внутренний класс элемента списка
    private static class Node<B> {
        B item;
        Node<B> next;
        Node<B> prev;

        Node(Node<B> prev, B element, Node<B> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    //Итератор по нисхождению
    public Iterator<B> descendingIterator() {
        return new DescendingIterator();
    }

    //Внутренний класс для нисходящего движения итератора
    private class DescendingIterator implements Iterator<B> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public B next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }

    private CustomLinkedList<B> superClone() {
        try {
            return (CustomLinkedList<B>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    //Возвращает поверхностную копию списка. Сами элементы не клонируются
    public Object clone() {
        CustomLinkedList<B> clone = superClone();

        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        for (Node<B> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    //Возвращает массив, содержащий все элементы списка в правильной последовательности. Возвращенный массив будет
    //"безопасным" в том смысле, что этот список не поддерживает никаких ссылок на него.
    //Другими словами, этот метод должен выделить новый массив. Таким образом, вызывающий объект может
    //изменять возвращаемый массив. Этот метод действует как мост между API-интерфейсами на основе массивов и коллекций
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<B> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

    //Возвращает массив, содержащий все элементы списка в правильной последовательности (от первого до последнего
    //элемента); тип возвращаемого массива во время выполнения совпадает с типом указанного массива.
    //Если список помещается в указанный массив, он возвращается в нем. В противном случае выделяется
    //новый массив с типом времени выполнения указанного массива и размером списка.
    //Если список помещается в указанный массив с запасом места (т. е. в массиве больше элементов, чем в списке),
    //элемент в массиве, следующий сразу за концом списка, устанавливается пустым.
    //Может генерировать:
    //ArrayStoreException - в случае, если тип времени выполнения указанного массива не является супертипом
    //типа времени выполнения каждого элемента списка.
    //NullPointerException - в случае, если указанный массив пустой
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<B> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    @java.io.Serial
    private static final long serialVersionUID = 876323262645176354L;

    //Сохраняет состояние списка в поток (то есть сериализует его)
    @java.io.Serial
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        s.defaultWriteObject();

        s.writeInt(size);

        for (Node<B> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    //Восстанавливает список из потока (то есть десериализует его)
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        s.defaultReadObject();

        int size = s.readInt();

        for (int i = 0; i < size; i++)
            linkLast((B)s.readObject());
    }

    //
    @Override
    public Spliterator<B> spliterator() {
        return new CustomLLSpliterator<>(this, -1, 0);
    }

    //Своя реализация Сплитератора
    static final class CustomLLSpliterator<B> implements Spliterator<B> {
        static final int BATCH_UNIT = 1 << 10;  // batch array size increment
        static final int MAX_BATCH = 1 << 25;  // max batch array size;
        final CustomLinkedList<B> list; // null OK unless traversed
        CustomLinkedList.Node<B> current;      // current node; null until initialized
        int est;              // size estimate; -1 until first needed
        int expectedModCount; // initialized when est set
        int batch;            // batch size for splits

        CustomLLSpliterator(CustomLinkedList<B> list, int est, int expectedModCount) {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEst() {
            int s;
            final CustomLinkedList<B> lst;
            if ((s = est) < 0) {
                if ((lst = list) == null)
                    s = est = 0;
                else {
                    expectedModCount = lst.modCount;
                    current = lst.first;
                    s = est = lst.size;
                }
            }
            return s;
        }

        public long estimateSize() { return (long) getEst(); }

        public Spliterator<B> trySplit() {
            CustomLinkedList.Node<B> p;
            int s = getEst();
            if (s > 1 && (p = current) != null) {
                int n = batch + BATCH_UNIT;
                if (n > s)
                    n = s;
                if (n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do { a[j++] = p.item; } while ((p = p.next) != null && j < n);
                current = p;
                batch = j;
                est = s - j;
                return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super B> action) {
            CustomLinkedList.Node<B> p; int n;
            if (action == null) throw new NullPointerException();
            if ((n = getEst()) > 0 && (p = current) != null) {
                current = null;
                est = 0;
                do {
                    B e = p.item;
                    p = p.next;
                    action.accept(e);
                } while (p != null && --n > 0);
            }
            if (list.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super B> action) {
            CustomLinkedList.Node<B> p;
            if (action == null) throw new NullPointerException();
            if (getEst() > 0 && (p = current) != null) {
                --est;
                B e = p.item;
                current = p.next;
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
}
