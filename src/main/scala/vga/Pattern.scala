package vga

import chisel3._

class Pattern extends Module with VGAParams {
  val io = IO(new Bundle {
    val slideVertical = Input(Bool())
    val slideHorizontal = Input(Bool())

    val rgb = new Bundle {
      val r = Output(UInt(8.W))
      val g = Output(UInt(8.W))
      val b = Output(UInt(8.W))
    }
    val vgaHS = Output(Bool())
    val vgaVS = Output(Bool())

    val hCount = Output(UInt(10.W))
    val vCount = Output(UInt(10.W))
  })

  val hOffsetCounter = RegInit(0.U(8.W))
  val vOffsetCounter = RegInit(0.U(8.W))
  val hOffset = RegInit(0.U(3.W))
  val vOffset = RegInit(0.U(3.W))

  val syncGen = Module(new SyncGen)
  val displayRegs = Module(new DisplayRegs)

  val displayEnable =
    (verticalBlank < syncGen.io.vCount) &&
      (horizontalBlank < syncGen.io.hCount) &&
      (syncGen.io.hCount < horizontalPeriod)

  displayRegs.io.frameCountUp := (syncGen.io.vCount === verticalBlank) && (syncGen.io.hCount === horizontalBlank)
  displayRegs.io.slideVertical := io.slideVertical
  displayRegs.io.slideHorizontal := io.slideHorizontal
  displayRegs.io.slide := displayEnable

  when(displayEnable) {
    io.rgb.r := displayRegs.io.rgb.r
    io.rgb.g := displayRegs.io.rgb.g
    io.rgb.b := displayRegs.io.rgb.b
  } otherwise {
    io.rgb.r := 0.U
    io.rgb.g := 0.U
    io.rgb.b := 0.U
  }

  io.vgaHS := syncGen.io.vgaHS
  io.vgaVS := syncGen.io.vgaVS

  io.hCount := syncGen.io.hCount
  io.vCount := syncGen.io.vCount

  def returnColor(color: Bool): UInt = {
    Mux(color, 255.U(8.W), 0.U(8.W))
  }
}
