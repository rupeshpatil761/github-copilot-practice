import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';

const Home = () => {
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [modal, setModal] = useState({ open: false, patientName: '', data: [], loading: false });

    useEffect(() => {
        fetchPatients();
    }, []);

    const fetchPatients = async () => {
        try {
            setLoading(true);
            const response = await axios.get('http://localhost:8080/api/patients');
            setPatients(response.data);
            setError(null);
        } catch (err) {
            setError('Failed to fetch patient details. Check backend application is running and accessible.');
            console.error('Error fetching patients:', err);
        } finally {
            setLoading(false);
        }
    };

    const viewClinicalData = async (patientId, firstName, lastName) => {
        setModal({ open: true, patientName: `${firstName} ${lastName}`, data: [], loading: true });
        try {
            const response = await axios.get(`http://localhost:8080/api/clinicaldata/patient/${patientId}`);
            setModal(prev => ({ ...prev, data: response.data, loading: false }));
        } catch (err) {
            setModal(prev => ({ ...prev, loading: false }));
            toast.error('Failed to load clinical data');
        }
    };

    const closeModal = () => setModal({ open: false, patientName: '', data: [], loading: false });

    const formatMeasuredDateTime = (item) => {
        const rawDateTime = item?.measuredDateTime || item?.measureDateTime || item?.measuredDatetime;
        if (!rawDateTime) return '-';

        const parsed = new Date(rawDateTime);
        if (Number.isNaN(parsed.getTime())) return rawDateTime;

        return parsed.toLocaleString();
    };

    const deletePatient = async (patientId, firstName, lastName) => {
        if (!window.confirm(`Are you sure you want to delete ${firstName} ${lastName}?`)) return;
        try {
            await axios.delete(`http://localhost:8080/api/patients/${patientId}`);
            setPatients(prev => prev.filter(p => p.id !== patientId));
            toast.success(`${firstName} ${lastName} deleted successfully`);
        } catch (err) {
            toast.error('Failed to delete patient');
        }
    };

    return (
        <div className="patient-page">
            <div className="patient-card home-card">
                <h1 className="home-page-title">Clinicals Dashboard</h1>
                <h2 className="patient-title">Patient Details</h2>

                {loading && <p className="form-message">Loading...</p>}
                {error && <p className="home-error">{error}</p>}

                {!loading && !error && (
                    <>
                        <div className="table-wrap">
                            <table className="patient-table">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>First Name</th>
                                        <th>Last Name</th>
                                        <th>Age</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {patients.length > 0 ? (
                                        patients.map((patient) => (
                                            <tr key={patient.id}>
                                                <td>{patient.id}</td>
                                                <td>{patient.firstName}</td>
                                                <td>{patient.lastName}</td>
                                                <td>{patient.age}</td>
                                                <td className="action-cell">
                                                    <Link to={`/add-clinicals/${patient.id}`} className="inline-link">
                                                        Add Clinical Data
                                                    </Link>
                                                    <span className="action-divider">|</span>
                                                    <button
                                                        className="inline-link-btn"
                                                        onClick={() => viewClinicalData(patient.id, patient.firstName, patient.lastName)}
                                                    >
                                                        View Clinical Data
                                                    </button>
                                                    <span className="action-divider">|</span>
                                                    <button
                                                        className="inline-link-btn delete-link"
                                                        onClick={() => deletePatient(patient.id, patient.firstName, patient.lastName)}
                                                    >
                                                        Delete
                                                    </button>
                                                </td>
                                            </tr>
                                        ))
                                    ) : (
                                        <tr>
                                            <td colSpan="5" className="empty-cell">No patients found</td>
                                        </tr>
                                    )}
                                </tbody>
                            </table>
                        </div>

                        <Link to="/add-patient" className="table-link">
                            Add New Patient
                        </Link>
                    </>
                )}
            </div>

            {/* Clinical Data Modal */}
            {modal.open && (
                <div className="modal-overlay" onClick={closeModal}>
                    <div className="modal-box" onClick={(e) => e.stopPropagation()}>
                        <div className="modal-header">
                            <h3 className="modal-title">Clinical Data — {modal.patientName}</h3>
                            <button className="modal-close" onClick={closeModal}>✕</button>
                        </div>

                        {modal.loading && <p className="form-message">Loading...</p>}

                        {!modal.loading && (
                            modal.data.length > 0 ? (
                                <div className="modal-table-wrap">
                                    <table className="patient-table">
                                        <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Component Name</th>
                                                <th>Component Value</th>
                                                <th>Measured Date/Time</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {modal.data.map((item) => (
                                                <tr key={item.id}>
                                                    <td>{item.id}</td>
                                                    <td>{item.componentName}</td>
                                                    <td>{item.componentValue}</td>
                                                    <td>{formatMeasuredDateTime(item)}</td>
                                                </tr>
                                            ))}
                                        </tbody>
                                    </table>
                                </div>
                            ) : (
                                <p className="form-message">No clinical data found for this patient.</p>
                            )
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Home;

