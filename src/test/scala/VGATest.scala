import chisel3._
import chisel3.util._
import chisel3.iotesters._
import vga.Top
import org.scalatest.{Matchers, FlatSpec}

class VGATest extends FlatSpec with Matchers {
  it should "complete" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name", "verilator"), () => new Top){
      c =>
        new VGAPeekPokeTester(c)
    } should be (true)
  }
}

class VGAPeekPokeTester(c: Top) extends PeekPokeTester(c) {
  poke("c.reset", 1)
  step(1)
  poke("c.reset", 0)


  (0 until 525).foreach {
    v =>
      (0 until 800).foreach {
        h =>
          if(h >= 160 && v >= 45) {
            val r = if(peek(c.io.r) == 255) 1 else 0
            val g = if(peek(c.io.g) == 255) 1 else 0
            val b = if(peek(c.io.b) == 255) 1 else 0
            val rgb = s"$b$g$r"

            print(s"$rgb ")

            step(1)
          }
      }

      println()
  }
}

