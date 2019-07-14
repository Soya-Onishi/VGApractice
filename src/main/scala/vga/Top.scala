
package vga

import chisel3._
import chisel3.core.withClockAndReset
import led.LedDecoder

class Top extends Module {
  val io = IO(new Bundle {
    val r = Output(UInt(8.W))
    val g = Output(UInt(8.W))
    val b = Output(UInt(8.W))
    val vgaHS = Output(Bool())
    val vgaVS = Output(Bool())
    val outclk = Output(Clock())

    val vgaHSDebug = Output(Bool())
    val vgaVSDebug = Output(Bool())
    val hCount = Output(Vec(3, UInt(7.W)))
    val vCount = Output(Vec(3, UInt(7.W)))
  })

  val hLedDec = Module(new LedDecoder(3))
  val vLedDec = Module(new LedDecoder(3))

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

    io.vgaHSDebug := pattern.io.vgaHS
    io.vgaVSDebug := pattern.io.vgaVS

    hLedDec.io.in := pattern.io.hCount
    vLedDec.io.in := pattern.io.vCount

    io.hCount := hLedDec.io.outs
    io.vCount := vLedDec.io.outs
  }

  io.outclk := phasedClock
}
