package fr.quentincillierre.hangman.model;

public record Word(String text, String category, String hint, Difficulty difficulty) {}