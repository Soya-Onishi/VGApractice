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

  /*
  when (syncGen.io.hCount < horizontalBlank) {
    hOffsetCounter := 0.U
    hOffset := 0.U
  } .elsewhen(hOffsetCounter === (horizontalSize - 1.U)) {
    hOffsetCounter := 0.U
    hOffset := hOffset + 1.U
  } .otherwise {
    hOffsetCounter := hOffsetCounter + 1.U
  }

  when (syncGen.io.vCount < verticalBlank) {
    vOffsetCounter := 0.U
    vOffset := 0.U
  } .elsewhen (syncGen.io.hCount === (horizontalBlank - 1.U)) {
    when(vOffsetCounter === (verticalSize - 1.U)) {
      vOffsetCounter := 0.U
      vOffset := vOffset + 1.U
    } .otherwise {
      vOffsetCounter := vOffsetCounter + 1.U
    }
  }
  */

  when(displayEnable) {
    //val color = vOffset ^ hOffset
    val color = "b100".U(3.W)
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

  def returnColor(color: Bool): UInt = {
    Mux(color, 255.U(8.W), 0.U(8.W))
  }
}
