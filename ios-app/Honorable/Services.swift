import Foundation

protocol EmbeddingService { var modelID: String { get }; var dimension: Int { get }; func embedText(_ text: String) async throws -> [Float]? }
protocol OCRService { func recognize(data: Data) async throws -> String }
protocol VideoAnalysisService { func analyze(url: URL) async throws -> [VideoFrame] }
protocol MediaIndexer { func synchronize() async throws -> IndexStats; func pause(); func cancel() }
protocol VectorIndex { func upsert(id: String, vector: [Float]); func nearest(_ vector: [Float], limit: Int) -> [(String, Float)] }
struct VideoFrame { let timestamp: TimeInterval; let ocr: String; let labels: [String]; let embedding: [Float]? }
struct IndexStats { let added: Int; let updated: Int; let deleted: Int }
struct IndexCompatibility { let schemaVersion = 1; let modelID = "TinyCLIP-ViT-8M-16-Text-3M-YFCC15M-int8"; let dimension = 512 }

/// Intentionally unavailable until the validated ONNX asset and iOS runtime are bundled.
struct TinyCLIPEmbeddingService: EmbeddingService {
    let modelID = "TinyCLIP-ViT-8M-16-Text-3M-YFCC15M-int8", dimension = 512
    func embedText(_ text: String) async throws -> [Float]? { nil }
}

enum TermsRisk: String { case low, medium, high, unknown }
struct TermsAnalysis { let risk: TermsRisk; let summary: String; let good: [String]; let watchOut: [String]; static let disclaimer = "Informational summary only; not legal advice." }

struct TermsAnalyzer {
    func analyze(_ text: String) -> TermsAnalysis {
        guard !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return .init(risk: .unknown, summary: "Add an agreement to analyze.", good: [], watchOut: []) }
        let clauses = text.components(separatedBy: ".")
        let warningWords = ["automatic renewal", "arbitration", "class action", "non-refundable", "share data", "terminate"]
        let watch = clauses.filter { clause in warningWords.contains { clause.localizedCaseInsensitiveContains($0) } }
        return .init(risk: watch.count >= 4 ? .high : watch.isEmpty ? .low : .medium, summary: "Local review found \(watch.count) clause(s) to examine.", good: clauses.filter { $0.localizedCaseInsensitiveContains("cancel") || $0.localizedCaseInsensitiveContains("refund") }, watchOut: watch)
    }
}
