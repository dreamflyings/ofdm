package ofdm

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled
import dsptools.numbers._
import ofdm.fft.{DITDecimType, FFTParams, SDFFFT, SDFFFTType}

class FreqDomainRX[T <: Data : Real : BinaryRepresentation]
(
  params: RXParams[T],
  counterOpt: Option[GlobalCycleCounter] = None
) extends MultiIOModule {
  val in = IO(Flipped(Decoupled(params.protoFFTIn)))
  val tlastIn = IO(Input(Bool()))
  val out = IO(Decoupled(params.protoFFTOut))
  val tlastOut = IO(Output(Bool()))

  val fftParams = FFTParams(
    numPoints = params.nFFT,
    protoIQ = params.protoFFTIn,
    protoTwiddle = params.protoTwiddle,
    fftType = SDFFFTType,
    decimType = DITDecimType,
    sdfRadix = 4,
    pipeline = true
  )
  val fft = Module(new SDFFFT(fftParams))
  fft.io.in <> in
}
