package com.mycompany.rutaexpress.estructures.lists;


public class List {
    private Node head;

    public List() {
        head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void AddFirst(Object data) {
        if (isEmpty()) {
            head = new Node(data);
        } else {
            Node n = new Node(data);
            n.setLink(head);
            head = n;
        }
    }

    public Node Last() {
        Node last = null;
        Node aux = head;
        while (aux != null) {
            last = aux;
            aux = aux.getLink();
        }
        return last;
    }

    public void AddLast(Object data) {
        if (isEmpty()) {
            head = new Node(data);
        } else {
            Node last = Last();
            Node n = new Node(data);
            last.setLink(n);
        }
    }

    public int Size() {
        int count = 0;
        Node aux = head;
        while (aux != null) {
            count++;
            aux = aux.getLink();
        }
        return count;
    }

    public Node getHead() {
        return head;
    }

    public Node Previous(Node search) {
        Node pre = null;
        Node aux = head;
        while (aux != null && !aux.equals(search)) {
            pre = aux;
            aux = aux.getLink();
        }
        return pre;
    }

    public void AddPos(Object data, int pos) {
        if (pos == 1) {
            AddFirst(data);
        } else {
            if (pos == Size() + 1) {
                AddLast(data);
            } else {
                Node aux = head;
                int count = 1;
                while (aux != null && count < pos) {
                    aux = aux.getLink();
                    count++;
                }
                Node pre = Previous(aux);
                Node n = new Node(data);
                pre.setLink(n);
                n.setLink(aux);
            }
        }
    }

    public boolean DeleteFirst()
    {
        if(isEmpty())
            return false;
        else
        {
            head=head.getLink();
            return true;
        }
    }

    public boolean DeleteLast()
    {
        if(isEmpty())
            return false;
        else
        {
            Node last = Last();
            Node pre = Previous(last);
            if(pre==null)
                head=null;
            else
                pre.setLink(null);
            return true;
        }
    }

    @Override
    public String toString() {
        String text = "";
        Node aux = head;
        while (aux != null) {
            text = text + aux.getData() + "\n";
            aux = aux.getLink();
        }
        return text;
    }
}
