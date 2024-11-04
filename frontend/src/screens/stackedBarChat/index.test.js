import React from "react";
import { render, screen, waitFor, act } from "@testing-library/react";
import apiClient from "../../components/baseUrl";
import StackedBarChart from "./index"; // Adjust the import based on your file structure

jest.mock("../../components/baseUrl"); // Mock the API client

describe("StackedBarChart Component", () => {
  beforeEach(() => {
    jest.clearAllMocks(); // Clear any previous mocks
  });

  it("renders the chart header", async() => {
    await act(async () => {
    render(<StackedBarChart />);
    });
    const headerElement = screen.getByText(/Inventory Summary at Locations/i);
    expect(headerElement).toBeInTheDocument();
  });


  it("handles API errors gracefully", async () => {
    apiClient.get.mockRejectedValue(new Error("Error fetching data"));

    await act(async () => {
      render(<StackedBarChart />);
    });

    // Verify that no chart is rendered or display a suitable fallback
    await waitFor(() => {
      // Check for console error if applicable
      // Since the original component does not render anything on error,
      // you might consider adding an error message in the component itself
      // For now, we will only ensure the API is called
      expect(apiClient.get).toHaveBeenCalledWith("/locations/stackedBarData");
    });
  });
});
