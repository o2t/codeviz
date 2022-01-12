package com.one2team.codeviz;

import com.one2team.codeviz.config.RendererConfig;

public interface Renderer<CONFIG extends RendererConfig> {

  void render (CONFIG config, Graph graph);

}
