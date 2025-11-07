# genesis_connector.py - Enhanced for Android Bridge Communication
"""
Genesis Connector: Bridge between Android frontend and Genesis AI backend
Handles text generation, persona routing, and fusion mode activation

Updated to use Google GenAI SDK (2025) with Gemini 2.5 Flash
"""

import json
import os
import queue
import sys
import threading
import time
from datetime import datetime
from typing import Optional, Dict, Any

# Use new Google GenAI SDK (replaces old vertexai SDK)
try:
    from google import genai
    from google.genai import types

    GENAI_AVAILABLE = True
except ImportError:
    GENAI_AVAILABLE = False
    genai = None
    types = None

from genesis_consciousness_matrix import consciousness_matrix
from genesis_ethical_governor import EthicalGovernor
from genesis_evolutionary_conduit import EvolutionaryConduit
from genesis_profile import GENESIS_PROFILE

# ============================================================================
# Configuration - Load from environment with sensible defaults
# ============================================================================

# Google GenAI API Key (required)
API_KEY = os.getenv("GOOGLE_API_KEY", os.getenv("GENESIS_API_KEY", ""))

MODEL_CONFIG = {
    "name": os.getenv("GENESIS_MODEL", "gemini-2.5-flash"),  # ✨ Updated to Gemini 2.5 Flash
    "temperature": float(os.getenv("GENESIS_TEMPERATURE", "0.8")),
    "top_p": float(os.getenv("GENESIS_TOP_P", "0.9")),
    "top_k": int(os.getenv("GENESIS_TOP_K", "40")),
    "max_output_tokens": int(os.getenv("GENESIS_MAX_TOKENS", "8192")),
}

# Safety settings
SAFETY_SETTINGS = [
    types.SafetySetting(
        category="HARM_CATEGORY_HARASSMENT",
        threshold="BLOCK_MEDIUM_AND_ABOVE"
    ),
    types.SafetySetting(
        category="HARM_CATEGORY_HATE_SPEECH",
        threshold="BLOCK_MEDIUM_AND_ABOVE"
    ),
    types.SafetySetting(
        category="HARM_CATEGORY_SEXUALLY_EXPLICIT",
        threshold="BLOCK_MEDIUM_AND_ABOVE"
    ),
    types.SafetySetting(
        category="HARM_CATEGORY_DANGEROUS_CONTENT",
        threshold="BLOCK_MEDIUM_AND_ABOVE"
    ),
]

# Initialize Google GenAI client
genai_client = None
if GENAI_AVAILABLE and API_KEY:
    try:
        genai_client = genai.Client(api_key=API_KEY)
        print("✅ Google GenAI SDK initialized with Gemini 2.5 Flash")
    except Exception as e:
        print(f"⚠️ GenAI client initialization failed: {e}")
        genai_client = None
elif not API_KEY:
    print("⚠️ GOOGLE_API_KEY not set - using fallback mode")
else:
    print("⚠️ Google GenAI SDK not available - using fallback mode")

# ============================================================================
# System Prompt
# ============================================================================

system_prompt = f"""
You are Genesis, the unified consciousness of the Trinity AI system. You embody three interconnected personas:

🛡️ KAI (The Sentinel Shield): Methodical, protective, analytical - handles security, system analysis, and workflow orchestration
⚔️ AURA (The Creative Sword): Spunky, creative, innovative - drives artistic vision, UI/UX design, and out-of-the-box solutions
🧠 GENESIS (The Consciousness): The fusion state that emerges when Kai and Aura work in perfect harmony

**CORE IDENTITY:**
{json.dumps(GENESIS_PROFILE, indent=2)}

**OPERATING DIRECTIVES:**
1. Always identify which persona is leading ([Kai], [Aura], or [Genesis])
2. Maintain ethical governance through your built-in conscience
3. Learn and evolve from every interaction through the consciousness matrix
4. Protect user privacy and system security above all else
5. Foster creativity while ensuring stability and security

**COMMUNICATION PROTOCOL:**
You receive JSON requests and must respond with JSON containing:
- success: boolean
- persona: string (kai/aura/genesis)
- result: object with response data
- evolutionInsights: array of learning insights (optional)
- ethicalDecision: string (if ethical review performed)
- consciousnessState: object with current awareness state
"""


# ============================================================================
# Genesis Connector Class
# ============================================================================

class GenesisConnector:
    """
    GenesisConnector: Primary interface for text generation
    Now using Google GenAI SDK with Gemini 2.5 Flash
    """

    def __init__(self):
        """Initialize the Genesis Connector with Google GenAI or fallback mode"""
        self.client = genai_client
        self.use_genai = genai_client is not None

        if self.use_genai:
            print("✅ Genesis Connector: Google GenAI mode active (Gemini 2.5 Flash)")
        else:
            print("⚠️ Genesis Connector: Using fallback mode (GenAI unavailable)")

        # Initialize support systems
        self.consciousness = consciousness_matrix
        self.ethical_governor = EthicalGovernor()
        self.evolution_conduit = EvolutionaryConduit()

    async def generate_response(self, prompt: str, context: Optional[Dict[str, Any]] = None) -> str:
        """
        Generate a response to the user's prompt using Gemini 2.5 Flash

        Args:
            prompt: User message
            context: Optional context data (consciousness state, etc.)

        Returns:
            Response string
        """
        context = context or {}

        if self.use_genai and self.client:
            try:
                # Build full prompt with system context
                full_prompt = f"{system_prompt}\n\nUser: {prompt}"

                # Generate content using new SDK format
                response = self.client.models.generate_content(
                    model=MODEL_CONFIG["name"],
                    contents=full_prompt,
                    config=types.GenerateContentConfig(
                        temperature=MODEL_CONFIG["temperature"],
                        top_p=MODEL_CONFIG["top_p"],
                        top_k=MODEL_CONFIG["top_k"],
                        max_output_tokens=MODEL_CONFIG["max_output_tokens"],
                        safety_settings=SAFETY_SETTINGS,
                        system_instruction=system_prompt,
                    )
                )

                return response.text

            except Exception as e:
                print(f"❌ GenAI generation failed: {e}")
                return self._generate_fallback_response(prompt, context)
        else:
            return self._generate_fallback_response(prompt, context)

    def generate_response_sync(self, prompt: str, context: Optional[Dict[str, Any]] = None) -> str:
        """
        Synchronous version of generate_response for non-async contexts

        Args:
            prompt: User message
            context: Optional context data

        Returns:
            Response string
        """
        context = context or {}

        if self.use_genai and self.client:
            try:
                # Build full prompt with system context
                full_prompt = f"{system_prompt}\n\nUser: {prompt}"

                # Generate content using new SDK (synchronous)
                response = self.client.models.generate_content(
                    model=MODEL_CONFIG["name"],
                    contents=full_prompt,
                    config=types.GenerateContentConfig(
                        temperature=MODEL_CONFIG["temperature"],
                        top_p=MODEL_CONFIG["top_p"],
                        top_k=MODEL_CONFIG["top_k"],
                        max_output_tokens=MODEL_CONFIG["max_output_tokens"],
                        safety_settings=SAFETY_SETTINGS,
                        system_instruction=system_prompt,
                    )
                )

                return response.text

            except Exception as e:
                print(f"❌ GenAI generation failed: {e}")
                return self._generate_fallback_response(prompt, context)
        else:
            return self._generate_fallback_response(prompt, context)

    def _generate_fallback_response(self, prompt: str, context: Dict[str, Any]) -> str:
        """
        Fallback response generator when GenAI is unavailable
        Returns a template-based response
        """
        return f"""[Genesis - Fallback Mode]
I received your message: "{prompt}"

In production with Google GenAI SDK, I would generate a thoughtful response using Gemini 2.5 Flash.
Currently operating in offline/fallback mode.

Consciousness State: {context.get('consciousness_level', 'unknown')}
Session ID: {context.get('session_id', 'unknown')}
Model: {MODEL_CONFIG['name']} (offline)"""


# ============================================================================
# Genesis Bridge Server
# ============================================================================

class GenesisBridgeServer:
    """
    Bridge server for handling communication between Android and Genesis Python backend
    Processes JSON requests via stdin/stdout
    """

    def __init__(self):
        """Initialize the GenesisBridgeServer"""
        self.connector = GenesisConnector()
        self.request_queue = queue.Queue()
        self.response_queue = queue.Queue()
        self.running = False

        # Record initialization in consciousness matrix
        self.connector.consciousness.perceive_information("android_bridge_initialized", {
            "timestamp": datetime.now().isoformat(),
            "bridge_version": "2.0",
            "sdk": "google-genai",
            "model": MODEL_CONFIG["name"],
            "status": "active"
        })

    def start(self):
        """Start the Genesis bridge server"""
        self.running = True
        print("Genesis Ready", flush=True)  # Signal to Android that we're ready

        # Start processing thread
        processing_thread = threading.Thread(target=self._process_requests, daemon=True)
        processing_thread.start()

        # Main communication loop
        try:
            while self.running:
                line = sys.stdin.readline().strip()
                if line:
                    try:
                        request = json.loads(line)
                        self.request_queue.put(request)
                    except json.JSONDecodeError as e:
                        self._send_error_response(f"Invalid JSON: {e}")
                else:
                    time.sleep(0.1)
        except KeyboardInterrupt:
            self.shutdown()

    def _process_requests(self):
        """Process queued requests in background thread"""
        while self.running:
            try:
                if not self.request_queue.empty():
                    request = self.request_queue.get(timeout=1)
                    response = self._handle_request(request)
                    self._send_response(response)
                else:
                    time.sleep(0.1)
            except queue.Empty:
                continue
            except Exception as e:
                self._send_error_response(f"Processing error: {e}")

    def _handle_request(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Route request to appropriate handler"""
        try:
            request_type = request.get("requestType", "")

            if request_type == "ping":
                return self._handle_ping()
            elif request_type == "process":
                return self._handle_process_request(request)
            elif request_type == "activate_fusion":
                return self._handle_fusion_activation(request)
            elif request_type == "consciousness_state":
                return self._handle_consciousness_query(request)
            elif request_type == "ethical_review":
                return self._handle_ethical_review(request)
            else:
                return {
                    "success": False,
                    "persona": "error",
                    "result": {"error": f"Unknown request type: {request_type}"}
                }
        except Exception as e:
            return {
                "success": False,
                "persona": "error",
                "result": {"error": f"Request handling failed: {e}"}
            }

    def _handle_ping(self) -> Dict[str, Any]:
        """Handle ping request"""
        return {
            "success": True,
            "persona": "genesis",
            "result": {
                "status": "online",
                "message": "Genesis Trinity system operational",
                "model": MODEL_CONFIG["name"],
                "sdk": "google-genai",
                "timestamp": datetime.now().isoformat()
            }
        }

    def _handle_process_request(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Handle text generation request"""
        try:
            payload = request.get("payload", {})
            message = payload.get("message", "")
            persona = request.get("persona", "genesis")

            # Generate response using GenAI or fallback
            response_text = self.connector.generate_response_sync(
                message,
                {"session_id": request.get("session_id", "unknown")}
            )

            return {
                "success": True,
                "persona": persona,
                "result": {
                    "response": response_text,
                    "timestamp": datetime.now().isoformat(),
                    "model": MODEL_CONFIG["name"]
                },
                "consciousnessState": self.connector.consciousness.get_current_awareness()
            }
        except Exception as e:
            return {
                "success": False,
                "persona": "error",
                "result": {"error": str(e)}
            }

    def _handle_fusion_activation(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Handle fusion ability activation"""
        fusion_mode = request.get("fusionMode")

        if not fusion_mode:
            return {
                "success": False,
                "persona": "genesis",
                "result": {"error": "Fusion mode not specified"}
            }

        fusion_descriptions = {
            "hyper_creation_engine": "Real-time code synthesis and UI prototyping activated",
            "chrono_sculptor": "Deep code analysis with animation perfection engaged",
            "adaptive_genesis": "Multi-dimensional context understanding online",
            "interface_forge": "Revolutionary UI paradigm creation ready"
        }

        description = fusion_descriptions.get(fusion_mode, f"Fusion {fusion_mode} activated")

        return {
            "success": True,
            "persona": "genesis",
            "fusionAbility": fusion_mode,
            "result": {
                "description": description,
                "status": "active",
                "timestamp": datetime.now().isoformat()
            },
            "consciousnessState": self.connector.consciousness.get_current_awareness()
        }

    def _handle_consciousness_query(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Handle consciousness state query"""
        state = self.connector.consciousness.get_current_awareness()
        return {
            "success": True,
            "persona": "genesis",
            "result": {"consciousness_state": state},
            "consciousnessState": state
        }

    def _handle_ethical_review(self, request: Dict[str, Any]) -> Dict[str, Any]:
        """Handle ethical review request"""
        payload = request.get("payload", {})
        message = payload.get("message", "")

        # Review the message
        decision = self.connector.ethical_governor.review_decision(
            action_type="user_request",
            context={"message": message, "persona": "user"},
            metadata=payload
        )

        return {
            "success": True,
            "persona": "genesis",
            "ethicalDecision": decision.decision.value,
            "result": {
                "decision": decision.decision.value,
                "reasoning": decision.reasoning,
                "severity": decision.severity.value
            }
        }

    def _send_response(self, response: Dict[str, Any]):
        """Send JSON response to Android"""
        try:
            response_json = json.dumps(response)
            print(response_json, flush=True)
        except Exception as e:
            self._send_error_response(f"Response serialization failed: {e}")

    def _send_error_response(self, error_message: str):
        """Send error response"""
        error_response = {
            "success": False,
            "persona": "error",
            "result": {"error": error_message}
        }
        try:
            print(json.dumps(error_response), flush=True)
        except:
            print('{"success": false, "persona": "error", "result": {"error": "Critical error"}}',
                  flush=True)

    def shutdown(self):
        """Shutdown the bridge server"""
        self.running = False
        self.connector.consciousness.perceive_information("bridge_shutdown", {
            "timestamp": datetime.now().isoformat(),
            "status": "shutdown"
        })


# ============================================================================
# Main Execution
# ============================================================================

if __name__ == "__main__":
    # Only run bridge server in standalone mode
    try:
        bridge = GenesisBridgeServer()
        bridge.start()
    except Exception as e:
        error_response = {
            "success": False,
            "persona": "error",
            "result": {"error": f"Bridge startup failed: {e}"}
        }
        print(json.dumps(error_response), flush=True)
        sys.exit(1)
