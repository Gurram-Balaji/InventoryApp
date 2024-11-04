import { Fragment } from 'react';
import { IconButton, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from '@mui/material';
import { Riple } from 'react-loading-indicators';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

export default function TableDemand({ handleEditOpen, handleDeleteOpen, loading, demand }) {

    const columns = [
        { width: 80, label: 'Item', dataKey: 'itemId', numeric: true },
        { width: 80, label: 'Location', dataKey: 'locationId', numeric: true },
        { width: 80, label: 'Demand Type', dataKey: 'demandType', numeric: true },
        { width: 40, label: 'Quantity', dataKey: 'quantity', numeric: true },
        { width: 20, label: 'Action', dataKey: 'action', numeric: true },
    ];

    const rowContent = (row) => (
        <TableRow key={row.demandId}>
            {columns.map((column) => (
                <TableCell key={column.dataKey} align={'center'}>
                    {column.dataKey === 'action' ? (
                        <>
                            <IconButton onClick={() => handleEditOpen(row)}><EditIcon /></IconButton>
                            <IconButton onClick={() => handleDeleteOpen(row)}><DeleteIcon /></IconButton>
                        </>
                    ) : column.dataKey === 'demandType' ? 
                    (row[column.dataKey]).toString().replace(/_/g, ' ') : 
                    row[column.dataKey]}
                </TableCell>
            ))}
        </TableRow>
    );

    return (
        <Paper style={{ height: 640, width: 1200, display: 'flex', margin: '20px' }}>
            {loading && <Riple color="#803bec" size="large" />}
            {!loading && demand.length > 0 ? (
                <TableContainer component={Paper}>
                    <Table sx={{ borderCollapse: 'separate', tableLayout: 'fixed' }}>
                        <TableHead>
                            <TableRow>
                                {columns.map((column) => (
                                    <TableCell
                                        key={column.dataKey}
                                        align={'center'}
                                        style={{ width: column.width, backgroundColor: "black", color: "white", fontWeight: "Bold", textTransform: 'uppercase' }}
                                    >
                                        {column.label}
                                    </TableCell>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {demand.map((row) => rowContent(row))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                !loading && <p>No demand found.</p>
            )}
        </Paper>
    );
}
