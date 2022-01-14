package com.one2team.codeviz.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.util.Types;
import com.one2team.codeviz.Injection;

import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toMap;

@SuppressWarnings ("rawtypes")
public class JacksonMapTypeResolver implements TypeIdResolver {

  private JavaType baseType;

  private Map<String, Class> mapping;

  private Map<Class, String> reverseMapping;

  public static <TYPE> RegistryBuilder<TYPE> newBuilder (Binder binder, Class<TYPE> baseType) {
    return newBuilder (binder, TypeLiteral.get (baseType));
  }

  public static <TYPE> RegistryBuilder<TYPE> newBuilder (Binder binder, TypeLiteral<TYPE> baseType) {
    Map<String, Class<? extends TYPE>> map = new HashMap<> ();
    return new RegistryBuilder<> () {
      @Override
      public RegistryBuilder<TYPE> registerType (String typeId, Class<? extends TYPE> typeClass) {
        map.put (typeId, typeClass);
        return this;
      }

      @Override
      public void done () {
        if (map.isEmpty ())
          return;

        MapBinder<String, Class> mb =
          newMapBinder (binder, TypeLiteral.get (String.class), newClassTypeLiteral (extractRootJacksonType (baseType.getRawType ())));

        map.forEach ((k, v) -> mb.addBinding (k).toInstance (v));
      }
    };
  }

  @SuppressWarnings ("unchecked")
  private static TypeLiteral<Class> newClassTypeLiteral (Class<?> type) {
    return (TypeLiteral<Class>) TypeLiteral.get (Types.newParameterizedType (Class.class, Types.subtypeOf (type)));
  }

  @Override
  public JsonTypeInfo.Id getMechanism () {
    return JsonTypeInfo.Id.CUSTOM;
  }

  @Override
  @SuppressWarnings ({ "unchecked", "rawtypes" })
  public void init (JavaType baseType) {
    this.baseType = baseType;
    this.mapping = (Map<String, Class>) Injection.getInjector ().getInstance (Key.get (Types.mapOf (
      TypeLiteral.get (String.class).getType (),
      newClassTypeLiteral (extractRootJacksonType (baseType.getRawClass ())).getType ())));

    this.reverseMapping = this.mapping.entrySet ().stream ().collect (toMap (Entry::getValue, Entry::getKey));
  }

  private static Class<?> extractRootJacksonType (Class<?> type) {
    return Streams.superClassesOf (type)
      .filter (c -> c.getAnnotation (JsonTypeIdResolver.class) != null)
      .findFirst ()
      .orElseThrow (IllegalStateException::new);
  }

  @Override
  public String idFromValue (Object value) {
    return idFromValueAndType (value, value.getClass ());
  }

  @Override
  public String idFromBaseType () {
    return idFromValueAndType (null, baseType.getRawClass ());
  }

  @Override
  public String idFromValueAndType (Object obj, Class<?> typeClass) {
    return ofNullable (reverseMapping.get (typeClass))
      .orElseThrow (() -> new IllegalStateException ("no mapping for class " + typeClass.getName ()));
  }

  @Override
  public JavaType typeFromId (DatabindContext context, String typeId) {
    return ofNullable (mapping.get (typeId))
      .map (typeClass -> context.getTypeFactory ().constructSpecializedType (baseType, typeClass))
      .orElseThrow (() -> new IllegalStateException ("no mapping for typeId id  " + typeId));
  }

  @Override
  public String getDescForKnownTypeIds () {
    return "register " + baseType + " as type id";
  }

  public interface RegistryBuilder<TYPE> {

    RegistryBuilder<TYPE> registerType (String typeId, Class<? extends TYPE> typeClass);

    void done ();

  }
}
