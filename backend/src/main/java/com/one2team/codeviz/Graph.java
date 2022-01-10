package com.one2team.codeviz;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class Graph {

  @Getter
  @Setter
  private Map<String, Node> nodes;

}
