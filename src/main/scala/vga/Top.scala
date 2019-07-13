package vga

import chisel3._
import chisel3.core.withClockAndReset

class Top extends Module {
  val io = IO(new Bundle {
    val r = Output(UInt(4.W))
    val g = Output(UInt(4.W))
    val b = Output(UInt(4.W))
  })

  val pll = Module(new pll)
  pll.io.inclk0 := clock
  pll.io.areset := reset

  val phasedClock = (pll.io.c0 && pll.io.locked).asClock()
  withClockAndReset(phasedClock, reset) {
    val pattern = Module(new Pattern)
    val rgb = pattern.io.rgb
    io.r := rgb.r
    io.g := rgb.g
    io.b := rgb.b
  }
}
