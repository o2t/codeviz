package com.one2team.codeviz;

import com.one2team.codeviz.config.RendererFilterConfig;

public interface RendererFilter<CONFIG extends RendererFilterConfig> {

  Graph filter (CONFIG config, Graph graph);

}
