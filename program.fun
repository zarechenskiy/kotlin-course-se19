fun g(y) {
	if (y < 1) { return 0}
	return g(y - 1)
}
println(g(23), 239)
