import java.io.Serial;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import jdk.internal.access.SharedSecrets;
import jdk.internal.util.ArraysSupport;

public class CustomArrayList<A> extends AbstractList<A>
        implements List<A>, RandomAccess, Cloneable, java.io.Serializable {

    @java.io.Serial
    private static final long serialVersionUID = 8683452581122892189L;
    //Инициализация внутренних переменных

    //Ёмкость экземпляра ArrayList по умолчанию
    private static final int DEFAULT_CAPACITY = 10;

    //Пустой экземпляр массива для создания пустого ArrayList
    private static final Object[] EMPTY_ELEMENTDATA = {};

    //Общий пустой экземпляр списка, используемый для пустых экземпляров, с размером по умолчанию
    //Используется для того, чтобы знать, насколько увеличится ArrayList при добавлении первого элемента
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    //Буфер списка, в котором хранятся элементы ArrayList. Емкость ArrayList равна длине этого буфера списка
    //Любой пустой ArrayList с elementData, равным DEFAULTCAPACITY_EMPTY_ELEMENTDATA, будет расширен до
    // DEFAULT_CAPACITY при добавлении первого элемента.
    transient Object[] elementData;

    //Размер массива
    private int size;

    //Создаём 3 конструктора на разные ситуации

    //Первый конструктор. Создает пустой список с указанной начальной емкостью
    //Генерит исключение IllegalArgumentException, если указанная начальная емкость отрицательна
    public CustomArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    //Второй конструктор. Создает пустой список с начальной емкостью, по умолчанию равной десяти
    public CustomArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    //Третий конструктор. Создает список, содержащий элементы указанной коллекции, в том порядке,
    // в котором они возвращаются итератором коллекции.
    //Параметры:
    //c – коллекция, элементы которой должны быть помещены в этот список
    //Генерирует исключение NullPointerException в случае, если указанная коллекция имеет значение null
    public CustomArrayList(Collection<? extends A> c, int fromIndex, int toIndex) {
        Object[] a = c.toArray();
        if ((size = a.length) != 0) {
            if (c.getClass() == CustomArrayList.class) {
                elementData = a;
            } else {
                elementData = Arrays.copyOf(a, size, Object[].class);
            }
        } else {
            //удаляем пустой список
            elementData = EMPTY_ELEMENTDATA;
        }
    }

    //Сокращает ёмкость этого экземпляра ArrayList до текущего размера списка
    //Эту операцию стоит использовать, чтобы минимизировать объем памяти для экземпляра ArrayList
    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)
                    ? EMPTY_ELEMENTDATA
                    : Arrays.copyOf(elementData, size);
        }
    }

    //При необходимости увеличивает емкость списка ArrayList, чтобы гарантировать минимальную емкость
    //Параметры:
    //minCapacity – желаемая минимальная емкость
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elementData.length
                && !(elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
                && minCapacity <= DEFAULT_CAPACITY)) {
            modCount++;
            grow(minCapacity);
        }
    }

    //Увеличивает ёмкость списка до минимальной
    //Параметры:
    //minCapacity – желаемая минимальная емкость
    //Может генерировать исключение OutOfMemoryError в случае, если minCapacity меньше нуля
    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            int newCapacity = ArraysSupport.newLength(oldCapacity,
                    minCapacity - oldCapacity, /* minimum growth */
                    oldCapacity >> 1           /* preferred growth */);
            return elementData = Arrays.copyOf(elementData, newCapacity);
        } else {
            return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
        }
    }

    //Вторая реализация метода. Увеличивает ёмкость списка на 1 при добавлении нового элемента
    private Object[] grow() {
        return grow(size + 1);
    }

    //Метод получения размера списка
    @Override
    public int size() {
        return size;
    }

    //Метод определения, является ли список пустым
    public boolean isEmpty() {
        return size == 0;
    }

    //Проверяет, содержит ли список указанный элемент, передаваемый в качестве параметра
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    //Возвращает индекс первого вхождения указанного элемента в списке или -1, если список не содержит данный элемент
    public int indexOf(Object o) {
        return indexOfRange(o, 0, size);
    }

    //Реализация метода поиска первого вхождения указанного элемента в списке
    int indexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        if (o == null) {
            for (int i = start; i < end; i++) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = start; i < end; i++) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    //Возвращает индекс последнего вхождения указанного элемента в списке или -1, если список не содержит данный элемент
    public int lastIndexOf(Object o) {
        return lastIndexOfRange(o, 0, size);
    }

    //Реализация метода поиска последнего вхождения указанного элемента в списке
    int lastIndexOfRange(Object o, int start, int end) {
        Object[] es = elementData;
        if (o == null) {
            for (int i = end - 1; i >= start; i--) {
                if (es[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = end - 1; i >= start; i--) {
                if (o.equals(es[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    //Метод копирования списка. Возвращает поверхностную копию списка ArrayList. Сами элементы не копируются
    //Данный метод возвращает Object, так что после его вызова потребуется сделать приведение к необходимому классу
    public Object clone() {
        try {
            CustomArrayList<?> v = (CustomArrayList<?>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            //Если что-то пошло не так во время копирования, генерируется исключение
            throw new InternalError(e);
        }
    }

    //Создаёт и возвращает новый массив, содержащий все элементы списка в правильной последовательности
    //(от первого до последнего элемента).
    //Этот метод действует как мост между API-интерфейсами на основе массивов и коллекций.
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    //Возвращает массив, содержащий все элементы в этом списке в правильной последовательности
    //(от первого до последнего элемента); тип возвращаемого массива во время выполнения совпадает с
    //типом указанного массива. Если список помещается в указанный массив, он возвращается в нем.
    //В противном случае выделяется новый массив с размером этого списка.
    //Если список помещается в указанный массив с лишним местом (т. е. в массиве больше элементов, чем в списке),
    //элемент в массиве, следующий сразу за концом коллекции, устанавливается пустым.
    //Это полезно при определении длины списка, однако, нужно точно знать, что список не содержит пустых элементов
    //Параметры:
    //a – массив, в который должны быть помещены элементы списка, если он достаточно велик;
    // в противном случае для этой цели выделяется новый массив того же типа
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            //Создаём новый массив с содержимым списка
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    //Возвращает значение элемента списка за индексом index
    A elementData(int index) {
        return (A) elementData[index];
    }

    //Возвращает сам элемент списка с заданным индексом
    static <A> A elementAt(Object[] es, int index) {
        return (A) es[index];
    }

    //Возвращает элемент в указанной позиции списка
    //Может генерировать исключение IndexOutOfBoundsException в случае, если индекс выходит за пределы
    //допустимого диапазона
    @Override
    public A get(int index) {
        Objects.checkIndex(index, size);
        return elementData(index);
    }

    //Заменяет элемент списка с заданным индексом указанным элементом
    //Может генерировать исключение IndexOutOfBoundsException в случае, если индекс выходит за пределы
    //допустимого диапазона
    public A set(int index, A element) {
        Objects.checkIndex(index, size);
        A oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    //Этот вспомогательный метод отделен от основного, чтобы размер байт-кода метода не превышал 35
    //(значение по умолчанию), что помогает, когда add(E) вызывается в цикле, скомпилированном C1
    private void add(A e, Object[] elementData, int s) {
        if (s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        size = s + 1;
    }

    //Добавляет указанный элемент в конец этого списка, возвращает значение true
    public boolean add(A e) {
        modCount++;
        add(e, elementData, size);
        return true;
    }

    //Вставляет указанный элемент в указанную позицию в этом списке. Сдвигает элемент, находящийся в данный момент
    //в этой позиции (если есть), и любые последующие элементы вправо (добавляет единицу к их индексам)
    //Может генерировать исключение IndexOutOfBoundsException в случае, если индекс выходит за пределы
    //допустимого диапазона
    public void add(int index, A element) {
        rangeCheckForAdd(index);
        modCount++;
        final int s;
        Object[] elementData;
        if ((s = size) == (elementData = this.elementData).length)
            elementData = grow();
        System.arraycopy(elementData, index,
                elementData, index + 1,
                s - index);
        elementData[index] = element;
        size = s + 1;
    }

    //Версия метода rangeCheck, используемая методами add и addAll
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    //Создает подробное сообщение IndexOutOfBoundsException
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    //Удаляет элемент в указанной позиции в этом списке. Сдвигает любые последующие элементы влево
    //(вычитает единицу из их индексов)
    //Может генерировать исключение IndexOutOfBoundsException в случае, если индекс выходит за пределы
    //допустимого диапазона
    public A remove(int index) {
        Objects.checkIndex(index, size);
        final Object[] es = elementData;

        A oldValue = (A) es[index];
        fastRemove(es, index);

        return oldValue;
    }

    //Частный метод удаления, который пропускает проверку границ и не возвращает удаленное значение
    private void fastRemove(Object[] es, int i) {
        modCount++;
        final int newSize;
        if ((newSize = size - 1) > i)
            System.arraycopy(es, i + 1, es, i, newSize - i);
        es[size = newSize] = null;
    }

    //Сравнивает указанный объект со списком на предмет равенства. Возвращает true тогда и только тогда,
    //когда указанный объект также является списком, оба списка имеют одинаковый размер и все соответствующие
    //пары элементов в двух списках равны.
    //Другими словами, два списка считаются равными, если они содержат одни и те же элементы в одном и том же порядке
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof List)) {
            return false;
        }

        final int expectedModCount = modCount;

        boolean equal = (o.getClass() == ArrayList.class)
                ? equalsArrayList((CustomArrayList<?>) o)
                : equalsRange((List<?>) o, 0, size);

        checkForComodification(expectedModCount);
        return equal;
    }

    //Внутренний метод сравнения ArrayList-ов
    private boolean equalsArrayList(CustomArrayList<?> other) {
        final int otherModCount = other.modCount;
        final int s = size;
        boolean equal;
        if (equal = (s == other.size)) {
            final Object[] otherEs = other.elementData;
            final Object[] es = elementData;
            if (s > es.length || s > otherEs.length) {
                throw new ConcurrentModificationException();
            }
            for (int i = 0; i < s; i++) {
                if (!Objects.equals(es[i], otherEs[i])) {
                    equal = false;
                    break;
                }
            }
        }
        other.checkForComodification(otherModCount);
        return equal;
    }

    //Метод проверки модификаций списков?
    private void checkForComodification(final int expectedModCount) {
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    //Сравнение ёмкостей списков
    boolean equalsRange(List<?> other, int from, int to) {
        final Object[] es = elementData;
        if (to > es.length) {
            throw new ConcurrentModificationException();
        }
        var oit = other.iterator();
        for (; from < to; from++) {
            if (!oit.hasNext() || !Objects.equals(es[from], oit.next())) {
                return false;
            }
        }
        return !oit.hasNext();
    }

    //Метод возвращает целочисленное представление адреса памяти объекта. По умолчанию этот метод возвращает
    //случайное целое число, уникальное для каждого списка. Это целое число может измениться между несколькими
    //запусками приложения и не останется прежним
    public int hashCode() {
        int expectedModCount = modCount;
        int hash = hashCodeRange(0, size);
        checkForComodification(expectedModCount);
        return hash;
    }

    //Метод вычисляет это случайное целое число
    int hashCodeRange(int from, int to) {
        final Object[] es = elementData;
        if (to > es.length) {
            throw new ConcurrentModificationException();
        }
        int hashCode = 1;
        for (int i = from; i < to; i++) {
            Object e = es[i];
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

    //Удаляет первое вхождение указанного элемента из списка, если он присутствует.
    //Если список не содержит элемента, он не изменяется. Возвращает true, если список содержит указанный
    //элемент (или если список изменился в результате вызова).
    //Параметры:
    //o — элемент, который нужно удалить из этого списка, если он присутствует
    public boolean remove(Object o) {
        final Object[] es = elementData;
        final int size = this.size;
        int i = 0;
        found: {
            if (o == null) {
                for (; i < size; i++)
                    if (es[i] == null)
                        break found;
            } else {
                for (; i < size; i++)
                    if (o.equals(es[i]))
                        break found;
            }
            return false;
        }
        fastRemove(es, i);
        return true;
    }

    //Удаляет все элементы из этого списка. Список будет пуст после возврата этого метода
    public void clear() {
        modCount++;
        final Object[] es = elementData;
        for (int to = size, i = size = 0; i < to; i++)
            es[i] = null;
    }

    //Добавляет все элементы указанной коллекции в конец списка в том порядке, в котором они возвращаются
    //итератором указанной коллекции.
    //Параметры:
    //c – коллекция, содержащая элементы, которые нужно добавить в этот список
    //Возвращает:
    //true, если этот список изменился в результате вызова
    //Может генерировать исключение NullPointerException в случае, если указанная коллекция имеет значение null
    public boolean addAll(Collection<? extends A> c) {
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Object[] elementData;
        final int s;
        if (numNew > (elementData = this.elementData).length - (s = size))
            elementData = grow(s + numNew);
        System.arraycopy(a, 0, elementData, s, numNew);
        size = s + numNew;
        return true;
    }

    //Второй вариант реализации
    //Вставляет все элементы указанной коллекции в список, начиная с указанной позиции. Сдвигает элемент,
    //находящийся в данный момент в этой позиции (если есть), и любые последующие элементы вправо
    //(увеличивает их индексы). Новые элементы появятся в списке в том порядке, в котором они возвращаются
    //указанным итератором коллекции.
    //Параметры:
    //index – индекс, по которому нужно вставить первый элемент из указанной коллекции
    //c – коллекция, содержащая элементы, которые будут добавлены в этот список
    //Возвращает:
    //true, если этот список изменился в результате вызова
    //Может генерировать исключения:
    //IndexOutOfBoundsException – если индекс выходит за пределы допустимого диапазона
    //NullPointerException — если указанная коллекция имеет значение null
    public boolean addAll(int index, Collection<? extends A> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Object[] elementData;
        final int s;
        if (numNew > (elementData = this.elementData).length - (s = size))
            elementData = grow(s + numNew);

        int numMoved = s - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index,
                    elementData, index + numNew,
                    numMoved);
        System.arraycopy(a, 0, elementData, index, numNew);
        size = s + numNew;
        return true;
    }

    //Удаляет из списка все элементы, индекс которых находится в промежутке от fromIndex включительно и
    //toIndex, не включая. Сдвигает все последующие элементы влево (уменьшает их индекс).
    //Этот метод сокращает список на элементы (toIndex - fromIndex). Если toIndex=fromIndex, метод не действует
    //Может генерировать исключение IndexOutOfBoundsException в случае, если fromIndex или toIndex
    //вне допустимого диапазона
    protected void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IndexOutOfBoundsException(
                    outOfBoundsMsg(fromIndex, toIndex));
        }
        modCount++;
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }

    //Вторая реализация метода
    private static String outOfBoundsMsg(int fromIndex, int toIndex) {
        return "From Index: " + fromIndex + " > To Index: " + toIndex;
    }

    //Стирает разрыв индексов от нижнего до верхнего после удаления элементов списка
    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        System.arraycopy(es, hi, es, lo, size - hi);
        for (int to = size, i = (size -= hi - lo); i < to; i++)
            es[i] = null;
    }

    //Удаляет из списка все его элементы, содержащиеся в указанной коллекции.
    //Параметры:
    //c – коллекция, содержащая элементы, которые нужно удалить из списка
    //Возвращает:
    //true, если этот список изменился в результате вызова
    //Может генерировать исключения:
    //ClassCastException — если класс элемента списка несовместим с указанной коллекцией (необязательно)
    //NullPointerException — если список содержит пустой элемент, а указанная коллекция не допускает
    //пустых элементов (необязательно), или если указанная коллекция является пустой
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false, 0, size);
    }

    //Реализация метода удаления
    boolean batchRemove(Collection<?> c, boolean complement,
                        final int from, final int end) {
        Objects.requireNonNull(c);
        final Object[] es = elementData;
        int r;

        for (r = from;; r++) {
            if (r == end)
                return false;
            if (c.contains(es[r]) != complement)
                break;
        }
        int w = r++;
        try {
            for (Object e; r < end; r++)
                if (c.contains(e = es[r]) == complement)
                    es[w++] = e;
        } catch (Throwable ex) {
            System.arraycopy(es, r, es, w, end - r);
            w += end - r;
            throw ex;
        } finally {
            modCount += end - w;
            shiftTailOverGap(es, w, end);
        }
        return true;
    }

    //Сохраняет в списке только те элементы, которые содержатся в указанной коллекции. Другими словами,
    //удаляет из списка все его элементы, не содержащиеся в указанной коллекции.
    //Параметры:
    //c – коллекция, содержащая элементы, которые необходимо сохранить в этом списке
    //Возвращает:
    //true, если этот список изменился в результате вызова
    //Может генерировать исключения:
    //ClassCastException — если класс элемента списка несовместим с указанной коллекцией (необязательно)
    //NullPointerException — если список содержит пустой элемент, а указанная коллекция не допускает пустых элементов
    //(необязательно) или если указанная коллекция является пустой
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true, 0, size);
    }

    //Записывает экземпляр ArrayList в поток
    //Параметры:
    //с - поток
    //Может генерировать исключение IOException в случае, если возникает ошибка ввода/вывода
    @Serial
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {

        int expectedModCount = modCount;
        s.defaultWriteObject();

        s.writeInt(size);

        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    //Считывает экземпляр ArrayList из потока
    //Параметры:
    //с - поток
    //Может генерировать исключения:
    //ClassNotFoundException — если не удалось найти класс сериализованного объекта
    //IOException – если возникает ошибка ввода/вывода
    @Serial
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {

        s.defaultReadObject();
        s.readInt();
        if (size > 0) {
            SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Object[].class, size);
            Object[] elements = new Object[size];

            for (int i = 0; i < size; i++) {
                elements[i] = s.readObject();
            }

            elementData = elements;
        } else if (size == 0) {
            elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new java.io.InvalidObjectException("Invalid size: " + size);
        }
    }

    //Возвращает итератор списка по элементам в этом списке (в правильной последовательности),
    //начиная с указанной позиции в списке. Указанный индекс указывает первый элемент, который будет возвращен
    //первоначальным вызовом метода next. Первоначальный вызов метода previous вернет элемент с указанным
    //индексом минус один
    //Может генерировать исключение IndexOutOfBoundsException в случае, если индекс выходит за пределы
    //допустимого диапазона
    public ListIterator<A> listIterator(int index) {
        rangeCheckForAdd(index);
        return new CustomArrayList.ListItr(index);
    }

    //Внутренний класс - реализация интерфейса ListIterator и наследование от другого внутреннего класса Itr
    //Функционал - продвижение в обратном порядке, установка курсора, добавить новый индекс при расширении списка
    private class ListItr extends Itr implements ListIterator<A> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public A previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = CustomArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (A) elementData[lastRet = i];
        }

        public void set(A e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                CustomArrayList.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(A e) {
            checkForComodification();

            try {
                int i = cursor;
                CustomArrayList.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    //Внутренний класс - реализация интерфейса Iterator
    //Функционал - продвижение по списку вперёд, удаление индекса из итератора в случае, если изменился список
    private class Itr implements Iterator<A> {
        int cursor;       // Индекс следующего возвращаемого элемента
        int lastRet = -1; // Индекс последнего возвращаемого элемента; -1 если не найдено
        int expectedModCount = modCount;

        Itr() {}

        public boolean hasNext() {
            return cursor != size;
        }

        public A next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = CustomArrayList.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (A) elementData[lastRet = i];
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                CustomArrayList.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super A> action) {
            Objects.requireNonNull(action);
            final int size = CustomArrayList.this.size;
            int i = cursor;
            if (i < size) {
                final Object[] es = elementData;
                if (i >= es.length)
                    throw new ConcurrentModificationException();
                for (; i < size && modCount == expectedModCount; i++)
                    action.accept(elementAt(es, i));
                cursor = i;
                lastRet = i - 1;
                checkForComodification();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    //Возвращает подсписок (часть списка) в диапазоне между  fromIndex включительно и toIndex, невключая
    //Если fromIndex и toIndex равны, возвращаемый список пуст
    public List<A> subList(int fromIndex, int toIndex) {
        //subListRangeCheck(fromIndex, toIndex, size);
        return new SubList<>(this, fromIndex, toIndex);
    }

    //Реализация внутренного метода по работе с частью списка, наследование от абстрактного класса AbstractList,
    //имплементация интерфейса RandomAccess
    private static class SubList<A> extends AbstractList<A> implements RandomAccess {
        private final CustomArrayList<A> root;
        private final CustomArrayList.SubList<A> parent;
        private final int offset;
        private int size;

        //Конструктор создания подсписка из списка
        public SubList(CustomArrayList<A> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        //Конструктор создания подсписка из другого подсписка
        private SubList(CustomArrayList.SubList<A> parent, int fromIndex, int toIndex) {
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = parent.modCount;
        }

        //Сеттер
        public A set(int index, A element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            A oldValue = root.elementData(offset + index);
            root.elementData[offset + index] = element;
            return oldValue;
        }

        //Геттер
        public A get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.elementData(offset + index);
        }

        //Узнаём размер подсписка
        public int size() {
            checkForComodification();
            return size;
        }

        //Добавляем элемент в подсписок
        public void add(int index, A element) {
            rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        //Удаляем элемент из подсписка
        public A remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            A result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        //Удаляем диапазон элементов из подсписка
        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            root.removeRange(offset + fromIndex, offset + toIndex);
            updateSizeAndModCount(fromIndex - toIndex);
        }

        //Добавляем все элементы коллекции в конец подсписка
        public boolean addAll(Collection<? extends A> c) {
            return addAll(this.size, c);
        }

        //Добавляем все элементы коллекции в определённое место подсписка
        public boolean addAll(int index, Collection<? extends A> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }
////////////////////////////////////////////////////////
        //Метод перемещения диапазона
        public void replaceAll(UnaryOperator<A> operator) {
            root.replaceAllRange(operator, offset, offset + size);
        }

        //Удаление всех элементов коллекции
        public boolean removeAll(Collection<?> c) {
            return batchRemove(c, false);
        }

        public boolean retainAll(Collection<?> c) {
            return batchRemove(c, true);
        }

        //Реализация метода удаления элемента коллекции
        private boolean batchRemove(Collection<?> c, boolean complement) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified =
                    root.batchRemove(c, complement, offset, offset + size);
            if (modified)
                updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        //Метод удаления при выполнении определённых условий
        public boolean removeIf(Predicate<? super A> filter) {
            checkForComodification();
            int oldSize = root.size;
            boolean modified = root.removeIf(filter, offset, offset + size);
            if (modified)
                updateSizeAndModCount(root.size - oldSize);
            return modified;
        }

        //Метод выгрузки подсписка в массив объектов
        public Object[] toArray() {
            checkForComodification();
            return Arrays.copyOfRange(root.elementData, offset, offset + size);
        }

        //Метод выгрузки подсписка в отдельный список
        public <T> T[] toArray(T[] a) {
            checkForComodification();
            if (a.length < size)
                return (T[]) Arrays.copyOfRange(
                        root.elementData, offset, offset + size, a.getClass());
            System.arraycopy(root.elementData, offset, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }

        //Метод сравнения
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }

            if (!(o instanceof List)) {
                return false;
            }

            boolean equal = root.equalsRange((List<?>)o, offset, offset + size);
            checkForComodification();
            return equal;
        }

        //Метод получения хэш-кода для более быстрого сравнения списков
        public int hashCode() {
            int hash = root.hashCodeRange(offset, offset + size);
            checkForComodification();
            return hash;
        }

        public int indexOf(Object o) {
            int index = root.indexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public int lastIndexOf(Object o) {
            int index = root.lastIndexOfRange(o, offset, offset + size);
            checkForComodification();
            return index >= 0 ? index - offset : -1;
        }

        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        public Iterator<A> iterator() {
            return listIterator();
        }

        //Класс итерирования подсписка
        public ListIterator<A> listIterator(int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            return new ListIterator<A>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = CustomArrayList.SubList.this.modCount;

                public boolean hasNext() {
                    return cursor != CustomArrayList.SubList.this.size;
                }

                public A next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= CustomArrayList.SubList.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (A) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                public A previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = root.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (A) elementData[offset + (lastRet = i)];
                }

                public void forEachRemaining(Consumer<? super A> action) {
                    Objects.requireNonNull(action);
                    final int size = CustomArrayList.SubList.this.size;
                    int i = cursor;
                    if (i < size) {
                        final Object[] es = root.elementData;
                        if (offset + i >= es.length)
                            throw new ConcurrentModificationException();
                        for (; i < size && root.modCount == expectedModCount; i++)
                            action.accept(elementAt(es, offset + i));
                        cursor = i;
                        lastRet = i - 1;
                        checkForComodification();
                    }
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        CustomArrayList.SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = CustomArrayList.SubList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(A e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        root.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(A e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        CustomArrayList.SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = CustomArrayList.SubList.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (root.modCount != expectedModCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        //
        /*public List<A> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new CustomArrayList<>(this, fromIndex, toIndex);
        }*/

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        private void checkForComodification() {
            if (root.modCount != modCount)
                throw new ConcurrentModificationException();
        }

        private void updateSizeAndModCount(int sizeChange) {
            CustomArrayList.SubList<A> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while (slist != null);
        }

        //Реализация класса spliterator для итерации и разделения списка
        public Spliterator<A> spliterator() {
            checkForComodification();

            // ArrayListSpliterator not used here due to late-binding
            return new Spliterator<A>() {
                private int index = offset; // current index, modified on advance/split
                private int fence = -1; // -1 until used; then one past last index
                private int expectedModCount; // initialized when fence set

                private int getFence() { // initialize fence to size on first use
                    int hi; // (a specialized variant appears in method forEach)
                    if ((hi = fence) < 0) {
                        expectedModCount = modCount;
                        hi = fence = offset + size;
                    }
                    return hi;
                }

                public CustomArrayList<A>.ArrayListSpliterator trySplit() {
                    int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
                    // ArrayListSpliterator can be used here as the source is already bound
                    return (lo >= mid) ? null : // divide range in half unless too small
                            root.new ArrayListSpliterator(lo, index = mid, expectedModCount);
                }

                public boolean tryAdvance(Consumer<? super A> action) {
                    Objects.requireNonNull(action);
                    int hi = getFence(), i = index;
                    if (i < hi) {
                        index = i + 1;
                        A e = (A)root.elementData[i];
                        action.accept(e);
                        if (root.modCount != expectedModCount)
                            throw new ConcurrentModificationException();
                        return true;
                    }
                    return false;
                }

                public void forEachRemaining(Consumer<? super A> action) {
                    Objects.requireNonNull(action);
                    int i, hi, mc; // hoist accesses and checks from loop
                    CustomArrayList<A> lst = root;
                    Object[] a;
                    if ((a = lst.elementData) != null) {
                        if ((hi = fence) < 0) {
                            mc = modCount;
                            hi = offset + size;
                        }
                        else
                            mc = expectedModCount;
                        if ((i = index) >= 0 && (index = hi) <= a.length) {
                            for (; i < hi; ++i) {
                                A e = (A) a[i];
                                action.accept(e);
                            }
                            if (lst.modCount == mc)
                                return;
                        }
                    }
                    throw new ConcurrentModificationException();
                }

                public long estimateSize() {
                    return getFence() - index;
                }

                public int characteristics() {
                    return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
                }
            };
        }
    }

    //Метод удаления при выполнении определённых условий
    public boolean removeIf(Predicate<? super A> filter) {
        return removeIf(filter, 0, size);
    }

    //Своя реализация цикла forEach для списка
    //Может генерировать NullPointerException в случае, если переданный параметр пустой
    public void forEach(Consumer<? super A> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = this.size;
        for (int i = 0; modCount == expectedModCount && i < size; i++)
            action.accept(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    //Конструктор создания устойчивого Сплитератора
    public Spliterator<A> spliterator() {
        return new CustomArrayList.ArrayListSpliterator(0, -1, 0);
    }

    //Реализация Сплитератора специально для ArrayList
    //Облегчает проход Сплитератора в режиме реального времени при изменении списка
    final class ArrayListSpliterator implements Spliterator<A> {

        private int index;
        private int fence; // -1 в самом начале; затем значение последнего индекса
        private int expectedModCount; // инициализируется, когда устанавливается переменная fence

        ArrayListSpliterator(int origin, int fence, int expectedModCount) {
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // инициализация переменной fence в первый заход
            int hi;
            if ((hi = fence) < 0) {
                expectedModCount = modCount;
                hi = fence = size;
            }
            return hi;
        }

        public CustomArrayList.ArrayListSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // разделение диапазона, если он большой
                    new CustomArrayList.ArrayListSpliterator(lo, index = mid, expectedModCount);
        }

        public boolean tryAdvance(Consumer<? super A> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                A e = (A)elementData[i];
                action.accept(e);
                if (modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super A> action) {
            int i, hi, mc;
            Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((a = elementData) != null) {
                if ((hi = fence) < 0) {
                    mc = modCount;
                    hi = size;
                }
                else
                    mc = expectedModCount;
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        A e = (A) a[i];
                        action.accept(e);
                    }
                    if (modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return getFence() - index;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    //Небольшая реализация набора битов
    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }
    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }
    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    //Удаляет все элементы, удовлетворяющие поиску, от индекса i включительно до конечного индекса не включая
    boolean removeIf(Predicate<? super A> filter, int i, final int end) {
        Objects.requireNonNull(filter);
        int expectedModCount = modCount;
        final Object[] es = elementData;
        for (; i < end && !filter.test(elementAt(es, i)); i++)
            ;
        if (i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            deathRow[0] = 1L;   // set bit 0
            for (i = beg + 1; i < end; i++)
                if (filter.test(elementAt(es, i)))
                    setBit(deathRow, i - beg);
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            modCount++;
            int w = beg;
            for (i = beg; i < end; i++)
                if (isClear(deathRow, i - beg))
                    es[w++] = es[i];
            shiftTailOverGap(es, w, end);
            return true;
        } else {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return false;
        }
    }

    public void replaceAll(UnaryOperator<A> operator) {
        replaceAllRange(operator, 0, size);
        modCount++;
    }


    private void replaceAllRange(UnaryOperator<A> operator, int i, int end) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        for (; modCount == expectedModCount && i < end; i++)
            es[i] = operator.apply(elementAt(es, i));
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    //Метода сортировки списка
    public void sort(Comparator<? super A> c) {
        final int expectedModCount = modCount;
        Arrays.sort((A[]) elementData, 0, size, c);
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
        modCount++;
    }
}
