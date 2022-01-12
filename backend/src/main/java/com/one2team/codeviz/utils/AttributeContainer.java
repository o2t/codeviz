package com.one2team.codeviz.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.Getter;

import static java.util.Optional.ofNullable;

public class AttributeContainer {

  @Getter
  private final Map<Class<?>, Object> attributes = new HashMap<> ();

  @SuppressWarnings ("unchecked")
  public <T> T getAttribute (Class<T> attributeClass, Supplier<T> attributeSupplier) {
    return (T) attributes.computeIfAbsent (attributeClass, c -> attributeSupplier.get ());
  }

  public <T> T getOrCreateAttribute (Class<T> attributeClass) {
    return getAttribute (attributeClass, () -> {
      try {
        return attributeClass.getDeclaredConstructor ().newInstance ();
      } catch (Exception e) {
        throw new RuntimeException (e);
      }
    });
  }

  public <T> void setAttribute (Class<T> attributeClass, T attribute) {
    attributes.put (attributeClass, attribute);
  }

  public <T> T getAttribute (Class<T> attributeClass) {
    return ofNullable (attributes.get (attributeClass))
      .map (attributeClass::cast)
      .orElse (null);
  }
}
