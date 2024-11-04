import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Available from './';
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';

jest.mock('../../components/baseUrl'); // Mocking the apiClient
jest.mock('../../components/Toast'); // Mocking the errorToast function

describe('Available Component', () => {
    beforeEach(() => {
        jest.clearAllMocks(); // Clear mocks before each test
    });

    test('renders correctly with initial state', () => {
        render(<Available />);

        // Check if the main title is present
        expect(screen.getByText(/availability/i)).toBeInTheDocument();

        // Check if the form fields and button are present
        expect(screen.getByLabelText(/search location/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/search item/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /submit/i })).toBeInTheDocument();
    });

    test('fetches locations on input change', async () => {
        const mockLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
            JSON.stringify({ locationId: '2', locationDesc: 'Location B' }),
        ];

        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: mockLocations } },
        });

        render(<Available />);

        const locationInput = screen.getByLabelText(/search location/i);
        fireEvent.change(locationInput, { target: { value: 'Location' } });

        await waitFor(() => {
            expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Location');
            expect(screen.getByText('Location A')).toBeInTheDocument();
            expect(screen.getByText('Location B')).toBeInTheDocument();
        });
    });

    test('fetches items on input change', async () => {
        const mockItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
            JSON.stringify({ itemId: '2', itemDescription: 'Item B' }),
        ];

        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: mockItems } },
        });

        render(<Available />);

        const itemInput = screen.getByLabelText(/search item/i);
        fireEvent.change(itemInput, { target: { value: 'Item' } });


    });

    test('shows error toast if location fetch fails', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Failed to fetch'));

        render(<Available />);
        const locationInput = screen.getByLabelText(/search location/i);
        fireEvent.change(locationInput, { target: { value: 'Location' } });

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Failed to fetch locations');
        });
    });

    test('shows error toast if item fetch fails', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Failed to fetch'));

        render(<Available />);
        const itemInput = screen.getByLabelText(/search item/i);
        fireEvent.change(itemInput, { target: { value: 'Item' } });

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Failed to fetch items');
        });
    });

    test('shows error toast if v1 or v2 availability fetch fails', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Failed to fetch'));

        render(<Available />);
        const itemInput = screen.getByLabelText(/search item/i);
        fireEvent.change(itemInput, { target: { value: 'Item' } });

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Failed to fetch items');
        });
    });

    test('submits correctly and fetches availability data V1', async () => {
        const mockLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
            JSON.stringify({ locationId: '2', locationDesc: 'Center A' }),
            JSON.stringify({ locationId: '3', locationDesc: 'Center' }),
        ];
        const mockItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];
        const mockAvailabilityResponse = {
            data: { status: 200, payload: { availableQty: 10, stockLevel: 'Green' } },
        };

        const mockSearchLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
        ];
        const mockSearchItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];


        apiClient.get
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:00:32.92903994",
                    "message": "User Found.",
                    "payload": "G Balaji"
                }
            })
            .mockResolvedValueOnce({ data: { payload: { content: mockLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockItems } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchItems } } })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:08:03.080809172",
                    "message": "Locations Found.",
                    "payload": {
                        "content": [
                            { "locationId": "1", "locationDesc": "Location A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:09:41.104201324",
                    "message": "Items Found.",
                    "payload": {
                        "content": [
                            { "itemId": "1", "itemDescription": "Item A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T17:55:11.501180273",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 10
                    }
                }
            }).mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T17:55:11.501180273",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 10
                    }
                }
            });

        render(<Available />);

        // Simulate fetching locations and items
        fireEvent.change(screen.getByLabelText(/search location/i), { target: { value: 'Location' } });
        expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Location');
        await waitFor(() => expect(screen.getByText('Location A')).toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/search item/i), { target: { value: 'Item' } });
        await waitFor(() => expect(screen.getByText('Item A')).toBeInTheDocument());

        // Simulate selecting location and item
        fireEvent.click(screen.getByText('Location A'));
        fireEvent.click(screen.getByText('Item A'));

        // Submit form
        fireEvent.click(screen.getByRole('button', { name: /submit/i }));


        expect(apiClient.get).toHaveBeenCalledWith('/availability/v1/1/1');
        await waitFor(() => expect(screen.getByText(/available quantity/i)).toBeInTheDocument(), { timeout: 500 });
        await waitFor(() => expect(screen.getByText('10')).toBeInTheDocument());

    });

    test('shows error toast if availability fetch fails', async () => {
        const mockLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
        ];
        const mockItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];

        apiClient.get
            .mockResolvedValueOnce({ data: { payload: { content: mockLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockItems } } })
            .mockRejectedValueOnce(new Error('Failed to fetch locations'));

        render(<Available />);

        // Simulate fetching locations and items
        fireEvent.change(screen.getByLabelText(/search location/i), { target: { value: 'Location' } });
        await waitFor(() => expect(screen.getByText('Location A')).toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/search item/i), { target: { value: 'Item' } });
        await waitFor(() => expect(screen.getByText('Item A')).toBeInTheDocument());

        // Simulate selecting location and item
        fireEvent.click(screen.getByText('Location A'));
        fireEvent.click(screen.getByText('Item A'));

        // Submit form
        fireEvent.click(screen.getByRole('button', { name: /submit/i }));

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to fetch locations");
        });
    });


    test('validates selection before submission', async () => {
        render(<Available />);

        // Simulate submitting without selecting item
        fireEvent.click(screen.getByRole('button', { name: /submit/i }));

        expect(errorToast).toHaveBeenCalledWith("Select the item.");
    });


    test('submits correctly and fetches availability data V2', async () => {
        const mockLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
            JSON.stringify({ locationId: '2', locationDesc: 'Center A' }),
            JSON.stringify({ locationId: '3', locationDesc: 'Center' }),
        ];
        const mockItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];
        const mockSearchLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
        ];
        const mockSearchItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];


        apiClient.get
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:00:32.92903994",
                    "message": "User Found.",
                    "payload": "G Balaji"
                }
            })
            .mockResolvedValueOnce({ data: { payload: { content: mockLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockItems } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchItems } } })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:08:03.080809172",
                    "message": "Locations Found.",
                    "payload": {
                        "content": [
                            { "locationId": "1", "locationDesc": "Location A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:09:41.104201324",
                    "message": "Items Found.",
                    "payload": {
                        "content": [
                            { "itemId": "1", "itemDescription": "Item A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T17:55:11.501180273",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 10
                    }
                }
            }).mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:39:52.310743619",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 32,
                        "stockLevel": "Yellow"
                    }
                }
            });

        render(<Available />);

        // Simulate fetching locations and items
        fireEvent.change(screen.getByLabelText(/search location/i), { target: { value: 'Location' } });
        expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Location');
        await waitFor(() => expect(screen.getByText('Location A')).toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/search item/i), { target: { value: 'Item' } });
        await waitFor(() => expect(screen.getByText('Item A')).toBeInTheDocument());

        // Simulate selecting location and item
        fireEvent.click(screen.getByText('Location A'));
        fireEvent.click(screen.getByText('Item A'));

        fireEvent.click(screen.getByRole('checkbox', {
            name: /threshold level/i
        }));

        // Submit form
        fireEvent.click(screen.getByRole('button', { name: /submit/i }));


        expect(apiClient.get).toHaveBeenCalledWith('/availability/v2/1/1');

        await waitFor(() => expect(screen.getByText(/available quantity/i)).toBeInTheDocument(), { timeout: 500 });
        await waitFor(() => expect(screen.getByText('32')).toBeInTheDocument());
        // Check the computed style of the element
        // Find the element containing the explanation text
        const explanationText = screen.getByText(/Red - Lower than the threshold level/i).parentElement;
        expect(explanationText).toBeInTheDocument();
        expect(explanationText).toHaveStyle({ color: 'rgb(117, 117, 120)' });
        // Find the element with the class "response"
        const responseDiv = screen.getByRole('heading', { name: /available quantity/i }).parentElement;

        // Check if the background color is gold
        expect(responseDiv).toHaveStyle({ backgroundColor: 'gold' });
    });



    test('submits correctly and fetches availability data V2 Red level', async () => {
        const mockLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
            JSON.stringify({ locationId: '2', locationDesc: 'Center A' }),
            JSON.stringify({ locationId: '3', locationDesc: 'Center' }),
        ];
        const mockItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];
        const mockSearchLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
        ];
        const mockSearchItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];


        apiClient.get
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:00:32.92903994",
                    "message": "User Found.",
                    "payload": "G Balaji"
                }
            })
            .mockResolvedValueOnce({ data: { payload: { content: mockLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockItems } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchItems } } })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:08:03.080809172",
                    "message": "Locations Found.",
                    "payload": {
                        "content": [
                            { "locationId": "1", "locationDesc": "Location A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:09:41.104201324",
                    "message": "Items Found.",
                    "payload": {
                        "content": [
                            { "itemId": "1", "itemDescription": "Item A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T17:55:11.501180273",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 10
                    }
                }
            }).mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:39:52.310743619",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 32,
                        "stockLevel": "Red"
                    }
                }
            });

        render(<Available />);

        // Simulate fetching locations and items
        fireEvent.change(screen.getByLabelText(/search location/i), { target: { value: 'Location' } });
        expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Location');
        await waitFor(() => expect(screen.getByText('Location A')).toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/search item/i), { target: { value: 'Item' } });
        await waitFor(() => expect(screen.getByText('Item A')).toBeInTheDocument());

        // Simulate selecting location and item
        fireEvent.click(screen.getByText('Location A'));
        fireEvent.click(screen.getByText('Item A'));

        fireEvent.click(screen.getByRole('checkbox', {
            name: /threshold level/i
        }));

        // Submit form
        fireEvent.click(screen.getByRole('button', { name: /submit/i }));


        expect(apiClient.get).toHaveBeenCalledWith('/availability/v2/1/1');

        await waitFor(() => expect(screen.getByText(/available quantity/i)).toBeInTheDocument(), { timeout: 500 });
        await waitFor(() => expect(screen.getByText('32')).toBeInTheDocument());
        // Find the element with the class "response"
        const responseDiv = screen.getByRole('heading', { name: /available quantity/i }).parentElement;

        // Check if the background color is orangered
        expect(responseDiv).toHaveStyle({ backgroundColor: 'orangered' });
    });

    test('submits correctly and fetches availability data V2 green level', async () => {
        const mockLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
            JSON.stringify({ locationId: '2', locationDesc: 'Center A' }),
            JSON.stringify({ locationId: '3', locationDesc: 'Center' }),
        ];
        const mockItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];
        const mockSearchLocations = [
            JSON.stringify({ locationId: '1', locationDesc: 'Location A' }),
        ];
        const mockSearchItems = [
            JSON.stringify({ itemId: '1', itemDescription: 'Item A' }),
        ];


        apiClient.get
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:00:32.92903994",
                    "message": "User Found.",
                    "payload": "G Balaji"
                }
            })
            .mockResolvedValueOnce({ data: { payload: { content: mockLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockItems } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchLocations } } })
            .mockResolvedValueOnce({ data: { payload: { content: mockSearchItems } } })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:08:03.080809172",
                    "message": "Locations Found.",
                    "payload": {
                        "content": [
                            { "locationId": "1", "locationDesc": "Location A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T18:09:41.104201324",
                    "message": "Items Found.",
                    "payload": {
                        "content": [
                            { "itemId": "1", "itemDescription": "Item A" }
                        ],
                        "page": {
                            "size": 8,
                            "number": 0,
                            "totalElements": 1,
                            "totalPages": 1
                        }
                    }
                }
            })
            .mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T17:55:11.501180273",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 10
                    }
                }
            }).mockResolvedValueOnce({
                data: {
                    "success": true,
                    "timestamp": "2024-10-14T19:39:52.310743619",
                    "message": "Available Quantity",
                    "payload": {
                        "itemId": "1",
                        "locationId": "1",
                        "availableQty": 32,
                        "stockLevel": "Green"
                    }
                }
            });

        render(<Available />);

        // Simulate fetching locations and items
        fireEvent.change(screen.getByLabelText(/search location/i), { target: { value: 'Location' } });
        expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Location');
        await waitFor(() => expect(screen.getByText('Location A')).toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/search item/i), { target: { value: 'Item' } });
        await waitFor(() => expect(screen.getByText('Item A')).toBeInTheDocument());

        // Simulate selecting location and item
        fireEvent.click(screen.getByText('Location A'));
        fireEvent.click(screen.getByText('Item A'));

        fireEvent.click(screen.getByRole('checkbox', {
            name: /threshold level/i
        }));

        // Submit form
        fireEvent.click(screen.getByRole('button', { name: /submit/i }));


        expect(apiClient.get).toHaveBeenCalledWith('/availability/v2/1/1');

        await waitFor(() => expect(screen.getByText(/available quantity/i)).toBeInTheDocument(), { timeout: 500 });
        await waitFor(() => expect(screen.getByText('32')).toBeInTheDocument());
        // Find the element with the class "response"
        const responseDiv = screen.getByRole('heading', { name: /available quantity/i }).parentElement;

        // Check if the background color is lime
        expect(responseDiv).toHaveStyle({ backgroundColor: 'lime' });
    });

});