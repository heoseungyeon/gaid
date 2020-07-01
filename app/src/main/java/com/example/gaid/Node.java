package com.example.gaid;

import java.util.ArrayList;
import java.util.Arrays;

public class Node {



    private String kind;
    private Node parent;
    private ArrayList<Node> neighbors;
    private int x;
    private int y;
    private Object data;

    public Node() {
        neighbors = new ArrayList<Node>();
        data = new Object();
    }

    public Node(int x, int y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Node(Node parent) {
        this();
        this.parent = parent;
    }

    public Node(Node parent, int x, int y) {
        this();
        this.parent = parent;
        this.x = x;
        this.y = y;
    }

    public ArrayList<Node> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ArrayList<Node> neighbors) {
        this.neighbors = neighbors;
    }

    public void addNeighbor(Node node) {
        this.neighbors.add(node);
    }

    public void addNeighbors(Node... node) {
        this.neighbors.addAll(Arrays.asList(node));
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean equals(Node n) {
        return this.x == n.x && this.y == n.y;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}