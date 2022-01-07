import * as d3 from 'd3'

// Lazily construct the package hierarchy from class names.
function packageHierarchy(classes) {
  var map = {};

  function find(name, data) {
    var node = map[name], i;
    if (!node) {
      node = map[name] = data || {name: name, children: []};
      if (name.length) {
        node.parent = find(name.substring(0, i = name.lastIndexOf(".")));
        node.parent.children.push(node);
        node.key = name.substring(i + 1);
      }
    }
    return node;
  }

  classes.forEach(function(d) {
    find(d.name, d);
  });

  return d3.hierarchy(map[""]);
}

// Return a list of imports for the given array of nodes.
function packageImports(nodes) {
  var map = {},
      imports = [];

  // Compute a map from name to node.
  nodes.forEach(function(d) {
    map[d.data.name] = d;
  });

  // For each import, construct a link from the source to target node.
  nodes.forEach(function(d) {
    if (d.data.imports) d.data.imports.forEach(function(i) {
      if (map[i]) {
        imports.push(map[d.data.name].path(map[i]));
      }
    });
  });

  return imports;
}

export function graphGenerator(flare) {
  // init d3 graph
  const diameter = 3000,
  radius = diameter / 2,
  innerRadius = radius - 300;

  const cluster = d3.cluster()
      .size([360, innerRadius]);

  const line = d3.lineRadial()
      .curve(d3.curveBundle.beta(0.85))
      .radius(function(d) { return d.y; })
      .angle(function(d) { return d.x / 180 * Math.PI; });

  const svg = d3.select(".App").append("svg")
      .attr("width", diameter)
      .attr("height", diameter)
    .append("g")
      .attr("transform", "translate(" + radius + "," + radius + ")");

  let link = svg.append("g").selectAll(".link"),
      node = svg.append("g").selectAll(".node");

  // read from JSON and create root
  const root = packageHierarchy(flare).sum(function(d) { return d.size })
  cluster(root);

  link = link
    .data(packageImports(root.leaves()))
    .enter().append("path")
      .each(function(d) { d.source = d[0], d.target = d[d.length - 1]; })
      .attr("class", "link")
      .attr("d", line)


  function mouseovered(d) {
    node
      .each(function(n) { n.target = n.source = false })

    link
      .classed("link--target", function(l) { if (l.target === d) return l.source.source = true })
      .classed("link--source", function(l) { if (l.source === d) return l.target.target = true })
      .filter(function(l) { return l.target === d || l.source === d })
      .raise()

    node
      .classed("node--target", (n) => n.target)
      .classed("node--source", (n) => n.source)
  }

  function mouseouted() {
    link
      .classed("link--target", false)
      .classed("link--source", false)

    node
      .classed("node--target", false)
      .classed("node--source", false)
  }

  node = node
    .data(root.leaves())
    .enter().append("text")
      .attr("class", "node")
      .attr("dy", "0.31em")
      .attr("transform", function(d) { return "rotate(" + (d.x - 90) + ")translate(" + (d.y + 8) + ",0)" + (d.x < 180 ? "" : "rotate(180)"); })
      .attr("text-anchor", function(d) { return d.x < 180 ? "start" : "end"; })
      .text(function(d) { return d.data.key; })
      .on("mouseover", mouseovered)
      .on("mouseout", mouseouted)
}
