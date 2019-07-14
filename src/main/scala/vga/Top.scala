
package vga

import chisel3._
import chisel3.core.withClockAndReset

class Top extends Module {
  val io = IO(new Bundle {
    val r = Output(UInt(8.W))
    val g = Output(UInt(8.W))
    val b = Output(UInt(8.W))
    val vgaHS = Output(Bool())
    val vgaVS = Output(Bool())
    val outclk = Output(Clock())
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

    io.vgaHS := pattern.io.vgaHS
    io.vgaVS := pattern.io.vgaVS
  }

  io.outclk := phasedClock
}
