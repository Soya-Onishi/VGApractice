package vga

import chisel3._

class SyncGen extends Module with VGAParams {
  val io = IO(new Bundle {
    val vgaHS = Output(Bool())
    val vgaVS = Output(Bool())
    val hCount = Output(UInt(10.W))
    val vCount = Output(UInt(10.W))
  })

  val vgaHS = RegInit(true.B)
  val vgaVS = RegInit(true.B)
  val hCount = RegInit(0.U(10.W))
  val vCount = RegInit(0.U(10.W))

  when (hCount === horizontalPeriod) {
    hCount := 0.U

    when (vCount === verticalPeriod) {
      vCount := 0.U
    } otherwise {
      vCount := vCount + 1.U
    }
  } otherwise {
    hCount := hCount + 1.U
  }

  when(hCount === horizontalSignalStart) {
    vgaHS := false.B

    when(vCount === verticalSignalStart) {
      vgaVS := false.B
    } otherwise {
      vgaVS := true.B
    }
  } otherwise {
    vgaHS := true.B
  }

  io.vgaVS := vgaVS
  io.vgaHS := vgaHS
  io.vCount := vCount
  io.hCount := hCount
}
